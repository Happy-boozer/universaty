package com.example.myapplication8956



import android.Manifest
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    // UI элементы
    private lateinit var btnGetAddress: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvAddress: TextView
    private lateinit var tvCoordinates: TextView
    private lateinit var tvError: TextView
    private lateinit var scrollViewResult: ScrollView

    // Клиент для определения местоположения
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Callback для получения местоположения
    private var locationCallback: LocationCallback? = null

    // Флаг для отслеживания состояния загрузки
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация UI элементов
        initViews()

        // Инициализация клиента местоположения
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Настройка обработчика нажатия кнопки
        btnGetAddress.setOnClickListener {
            if (!isLoading) {
                checkLocationPermissionAndGetAddress()
            }
        }
    }

    /**
     * Инициализация элементов пользовательского интерфейса
     */
    private fun initViews() {
        btnGetAddress = findViewById(R.id.btnGetAddress)
        progressBar = findViewById(R.id.progressBar)
        tvAddress = findViewById(R.id.tvAddress)
        tvCoordinates = findViewById(R.id.tvCoordinates)
        tvError = findViewById(R.id.tvError)
        scrollViewResult = findViewById(R.id.scrollViewResult)

        // Скрываем элементы результатов и ошибок при запуске
        scrollViewResult.visibility = View.GONE
        tvError.visibility = View.GONE
    }

    /**
     * Проверка разрешений и запуск получения адреса
     */
    private fun checkLocationPermissionAndGetAddress() {
        // Проверяем наличие разрешений
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешения уже есть, получаем местоположение
                getCurrentLocation()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Объясняем пользователю, зачем нужно разрешение
                showPermissionRationale()
            }
            else -> {
                // Запрашиваем разрешения
                requestLocationPermissions()
            }
        }
    }

    /**
     * Показываем объяснение необходимости разрешений
     */
    private fun showPermissionRationale() {
        Toast.makeText(
            this,
            R.string.toast_permission_required,
            Toast.LENGTH_LONG
        ).show()

        // Запрашиваем разрешения после объяснения
        requestLocationPermissions()
    }

    /**
     * Запрос разрешений на определение местоположения
     */
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Получение текущего местоположения
     */
    private fun getCurrentLocation() {
        // Показываем индикатор загрузки
        showLoading(true)

        // Проверяем доступность GPS
        if (!isGPSEnabled()) {
            showError(getString(R.string.error_gps_disabled))
            showLoading(false)
            return
        }

        // Проверяем доступность интернета (для Geocoder)
        if (!isNetworkAvailable()) {
            showError(getString(R.string.error_no_internet))
            showLoading(false)
            return
        }

        // Создаем запрос на получение местоположения
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        )
            .setMinUpdateIntervalMillis(5000)
            .build()

        // Создаем callback для получения местоположения
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Останавливаем получение обновлений
                locationCallback?.let {
                    fusedLocationClient.removeLocationUpdates(it)
                }

                val location = locationResult.lastLocation
                if (location != null) {
                    // Получили координаты, теперь выполняем обратное геокодирование
                    reverseGeocode(location.latitude, location.longitude)
                } else {
                    showError(getString(R.string.error_location_not_found))
                    showLoading(false)
                }
            }
        }

        // Запрашиваем обновления местоположения
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback as LocationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            showError(getString(R.string.error_permission_denied))
            showLoading(false)
        }
    }

    /**
     * Получение последнего известного местоположения (альтернативный метод)
     */
    private fun getLastKnownLocation() {
        try {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    reverseGeocode(location.latitude, location.longitude)
                } else {
                    // Если нет последнего известного местоположения, запрашиваем новое
                    getCurrentLocation()
                }
            }
        } catch (e: SecurityException) {
            showError(getString(R.string.error_permission_denied))
            showLoading(false)
        }
    }

    /**
     * Обратное геокодирование - преобразование координат в адрес
     */
    private fun reverseGeocode(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                // Формируем читаемый адрес
                val fullAddress = buildAddressString(address)

                // Формируем строку с координатами
                val coordinates = String.format(
                    Locale.getDefault(),
                    "lat: %.6f\nlng: %.6f",
                    latitude,
                    longitude
                )

                // Отображаем результаты
                displayResults(fullAddress, coordinates)
            } else {
                showError(getString(R.string.error_address_not_found))
            }
        } catch (e: IOException) {
            showError("${getString(R.string.error_geocoder)}: ${e.message}")
        } finally {
            showLoading(false)
        }
    }

    /**
     * Формирование полного адреса из объекта Address
     */
    private fun buildAddressString(address: Address): String {
        val sb = StringBuilder()

        // Улица и номер дома
        address.thoroughfare?.let { thoroughfare ->
            sb.append(thoroughfare)
            address.featureName?.takeIf { it != thoroughfare }?.let { featureName ->
                sb.append(" $featureName")
            }
            sb.append("\n")
        }

        // Населенный пункт
        address.locality?.let { locality ->
            sb.append(locality)
            address.postalCode?.let { postalCode ->
                sb.append(", $postalCode")
            }
            sb.append("\n")
        }

        // Регион/область
        address.adminArea?.let { adminArea ->
            sb.append(adminArea)
            if (address.countryName != null) {
                sb.append(", ")
            }
        }

        // Страна
        address.countryName?.let { countryName ->
            sb.append(countryName)
        }

        return sb.toString().trim()
    }

    /**
     * Отображение результатов
     */
    private fun displayResults(address: String, coordinates: String) {
        tvAddress.text = address
        tvCoordinates.text = coordinates

        // Показываем результаты и скрываем ошибки
        scrollViewResult.visibility = View.VISIBLE
        tvError.visibility = View.GONE
    }

    /**
     * Отображение ошибки
     */
    private fun showError(errorMessage: String) {
        tvError.text = errorMessage
        tvError.visibility = View.VISIBLE
        scrollViewResult.visibility = View.GONE
    }

    /**
     * Управление отображением индикатора загрузки
     */
    private fun showLoading(show: Boolean) {
        isLoading = show

        if (show) {
            progressBar.visibility = View.VISIBLE
            btnGetAddress.isEnabled = false
            btnGetAddress.text = getString(R.string.button_loading)
        } else {
            progressBar.visibility = View.GONE
            btnGetAddress.isEnabled = true
            btnGetAddress.text = getString(R.string.button_get_address)
        }
    }

    /**
     * Проверка включен ли GPS
     */
    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
    }

    /**
     * Проверка доступности интернета
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * Обработка результата запроса разрешений
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение получено, получаем местоположение
                    getCurrentLocation()
                } else {
                    // Пользователь отказал в разрешении
                    showError(getString(R.string.error_permission_denied))
                    Toast.makeText(
                        this,
                        R.string.toast_permission_required,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Очистка ресурсов при уничтожении активности
     */
    override fun onDestroy() {
        super.onDestroy()
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    }


fun Location.toCoordinatesString(): String {
    return String.format(Locale.getDefault(), "lat: %.6f\nlng: %.6f", latitude, longitude)
}

fun Address.toAddressString(): String {
    return buildString {
        thoroughfare?.let {
            append(it)
            featureName?.takeIf { it != thoroughfare }?.let { feature -> append(" $feature") }
            appendLine()
        }
        locality?.let {
            append(it)
            postalCode?.let { code -> append(", $code") }
            appendLine()
        }
        adminArea?.let { admin ->
            append(admin)
            if (countryName != null) append(", ")
        }
        countryName?.let { append(it) }
    }.trim()
}
