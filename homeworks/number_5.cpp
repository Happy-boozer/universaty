#include <iostream>
using namespace std;
int NOD() {
    int a, b;
    cout << "" << "\n";
    cin >> a;
    cin >> b;
    while (a != 0 and b != 0)
    {
        if (a > b)
            a = a % b;
        else
            b = b % a;
    }
    if (not(a != 0 and b != 0)) {
        cout << a + b << "\n";

        
    }
    return 1;
}

int main()
{
    NOD();
}

bool RESHETO(int n) {
    int n = 0, m = 0;
cin >> n;
//int r = n + 3;
int* arr = new int[n+1];//выделение памяти под числа
bool* arr_2 = new bool[n + 1];//выделение под флаги
for (int i = 2; i < n+1; i++) {
    arr[i] = i;
    arr_2[i] = true;//забиваю оба массива
}
//cout << arr[n] << "\n";
for (int j = 2; j < n; j++) {
    //arr[j] = j;
    //cout << arr[j] << "\n";
    for (int k = 3; k < n+1; k++) {
        //cout << arr[k] << "\n";
        
        if (arr[k] % arr[j] == 0 and arr[k] != arr[j]) {// проверяю на сложность
            //cout << arr[k] << arr[j] << "\n";
            arr_2[k] = false;// опускаю флаг
            //delete[k] arr_2;
        }
    }
}
for (int i = 2; i < n + 1; i++) {
    if (arr_2[i])// считаю сколько их там
        m += 1;
}
delete[] arr;
delete[] arr_2;//очищаю стек
//cout << arr << "\n";
//m = sizeof(*arr_2) / sizeof(arr_2[0]); //length calculation
//m = sizeof(*arr);
cout<< m;
return;
}
