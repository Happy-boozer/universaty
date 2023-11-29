#define _USE_MATH_DEFINES
#include <iostream>
#include <math.h>
#include<Windows.h>
#include <fstream>
#include <string>
#include <map>
using namespace std;

void task_1() {
    int  sam=0;
    char data[50];
    ofstream MyFile("HW4.txt");
    for (int i = 0; i < 10;i++) {
        MyFile << i << "\n";
    }
    MyFile.close();

    ifstream cuca("HW4.txt");
    if (cuca.is_open()) {
        while (cuca.getline(data, 50)) {
            sam += atoi(data);
            //cout << sam << "\n";
            }
        cout << sam << "\n";
        cuca.close();
    }
    else
    {
        cout << "файл не удалось открыть" << "\n";
    }
    
    
}

int signer(int ch) {
    if (ch > 0)
        return 1;
    if (ch == 0)
        return 2;
    if (ch < 0)
        return 3;
    return 0;
}

void task_2() {
    int a=0;
    
    cout << "Введите число" << "\n";
    cin >> a;
    int sipnh =  signer(a);
    //cout << sipnh << "\n";
    switch (sipnh)
    {
    case 1:
        cout << "1" << "\n";
        break;
    case 2:
        cout << "0" << "\n";
        break;
    case 3:
        cout << "-1" << "\n";
        break;
    };
}

void task_3() {//-????????????????????????????????
    int type;
    cout << "введите номер соответсвующий типу фигуры прямоугольник - 1; треукгольник - 2; круг - 3" << "\n";
    cin >> type;
    switch (type) {
        case 1:
            float a, b;
            cout << "введите стороны прямоугольника пожалуйста" << "\n";
            cin >> a;
            cin >> b;
            if ((a > 0 and b > 0) or (a<0 and b <0))
                cout << a * b << "\n";
            else
                cout << "oops" << "\n";
            break;
        case 2:
            float q, c, h;
            cout << "введите сторону и высоту треугольника пожалуйста" << "\n";
            cin >> c;
            cin >> h;
            q = 0.5 * a * h;
            if (q>0)
                cout << 0.5 * a * h << "\n";
            else
                cout << "oops" << "\n";
            break;
        case 3:
            float r, p;
            p = M_PI;
            cout << "введите радиус круга пожалуйста" << "\n";
            cin >> r;
            if (r>0)
                cout << p * pow(r, 2) << "\n";
            else
                cout << "oops" << "\n";
            break;

    }

}

void task_4() {
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 12; j++) 
            cout << "*";
        for (int k = 0; k < 20; k++) 
            cout << "_";
        for (int k = 0; k < 1; k++)
            cout << "_" << "\n";
        
    }
    for (int r = 0; r < 9; r++) {
        for (int l = 0; l < 32; l++)
            cout << "_";
        for (int g = 0; g < 1; g++)
            cout << "_" << "\n";
    }
    return;
}

void task_5() {
    HWND hwnd = GetConsoleWindow();
    HDC hdc = GetDC(hwnd);
    HPEN Pen = CreatePen(PS_SOLID, 2, RGB(255, 255, 255));//цвет
    SelectObject(hdc,Pen);
    MoveToEx(hdc, 10, 85, NULL);
    LineTo(hdc, 200, 85);
    MoveToEx(hdc, 100, 0, NULL);
    LineTo(hdc, 100, 200);
    for (float x = -1.0f; x <= 9.0f; x += 0.01f) {// масштаб
        MoveToEx(hdc, 10*x + 100,-10*sin(x) + 85, NULL);
        LineTo(hdc, 10 * x + 100, -10 * sin(x) + 85);
    }
    ReleaseDC(hwnd, hdc);
    cin.get();
    int x = 0;
    for (float i = 0; i < 3.14 * 10; i += 0.05)
    {
        SetPixel(hdc, x, 100 + 50 * cos(i), RGB(255, 255, 255));
        x += 3;
    }

    ReleaseDC(hwnd, hdc);
    cin.ignore();
    return ;

}

string ToRoman(int anArabic) {
    int levels[] = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9,5, 4, 1 };
    map <int, string> symbol = { {1000,"M"},
                    {900, "CM"},
                    {500, "D"},
                    {400, "CD"},
                    {100, "C"},
                    {90, "XC"},
                    {50, "L"},
                    {40, "XL"},
                    {10, "X"},
                    {9, "IX"},
                    {5, "V"},
                    {4, "IV"},
                    {1, "I"} };

    string result;
    string r = to_string(anArabic);
    //for (int i = 0; i < r.length(); i++) {
    //    string stro="";
    //    //cout << r[i] << "\n";
    //    if (r[i] == '1')
    //        stro += symbol[1 * pow(10, i)];
    //}
    for (const int& level : levels) {
        if (anArabic >= level) {
            do {
                result += symbol[level];
                anArabic -= level;
            } while (anArabic >= level);
        }
    }
    return result;
}

void task_6() {
    string sign;
    string new_RIM;
    string signs[7]{"I", "V", "X", "L", "C", "D", "M"};
    int indexes[20];
    int numbers[7]{1, 5, 10, 50, 100, 500, 1000};
    cin >> sign; // введёное число
    for (int i = 0; i < sign.length()+1; i++) {
        for (int j = 0; j < 7; j++) {
            if (string{sign[i]} == signs[j]) {
                indexes[i] = j;
                //cout << signs[j] << "\n";
                //cout << string{sign[i]} << "\n";
        }
        
        }
        
    }
    int res = 0; // промежуточное значение
    for (int k = 0; k < sign.length(); k++) {
        //cout << "index="<<indexes[k] << "\n";
        //cout << "rim="<<signs[indexes[k]] << "\n";
        //cout << "cifra"<<numbers[indexes[k]] << "\n";
        if (indexes[k] < indexes[k + 1]) {
            res -= numbers[indexes[k]];           
        }
        else {
            res += numbers[indexes[k]];            
        }
        
    }

    cout << res << "\n";
    new_RIM = ToRoman(res);
    //cout << new_RIM << "\n";
    if (sign == new_RIM)
        cout << res << "\n";
    else {
        cout << "кажись нет такого" << "\n";
    }
    return;
    
}

int gets(int s, int m, int b, int c) {//stack overflow
    if (s <= 0)
        return 0;
    if(s <= 3900)
        return (m * gets(s - 1, m,b,c) + b) % c;
}
void task_7() {
    int s, var;
    cout << "rjkvo" << "\n";
    cin >> s;
    cout << "1- pobolshe, 2-gjvtymit" << "\n";
    cin >> var;
    switch (var)
    {
    case 1:
        cout << gets(s, 37, 3, 64) << "\n";

    case 2:
        cout << gets(s, 25173, 13849, 65537) << "\n";
    }
}

void task_8() {
    float prod[3][4]{
    {5,2,0,10},
    {3,5,2,5},
    {20, 0, 0, 0}
    };

    float cen[4][2]{
        {1.20, 0.50},
        {2.80, 0.40},
        {5.00, 1.00},
        {2.00, 1.50}
    };
    float profit[3][2]{
        {0,0},
        {0,0},
        {0,0} };
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 4; j++) {
            profit[i][0] += prod[i][j] * cen[j][0];
            profit[i][1] += prod[i][j] * cen[j][1];


        }
    }
    /*for (int l = 0; l < 3; l++) {
        for (int k = 0; k < 2; k++) {
            cout << profit[l][k] << "\n";
        }*/
    float Bolshie_dengi=0, Malenkie_dengi, Bolshie_komissionnie=0, Malenkie_kommissionie, vse_prodazji=0, vse_komissionnie=0, vse_dengi; 
    int index_B_D_P, index_M_D_P, index_B_K_P, index_M_K_P;
    for (int i = 0; i < 3;i++) {
        if(profit[i][0] > profit[i-1][0])
            index_B_D_P = i;
        if (profit[i][0] < profit[i - 1][0])
            index_M_D_P = i;
        if (profit[i][1] > profit[i-1][1])
            index_B_K_P = i;
        if (profit[i][1] < profit[i - 1][1])
            index_M_K_P = i;
        vse_prodazji += profit[i][0];
        vse_komissionnie += profit[i][1];

    }
    vse_dengi = vse_prodazji + vse_komissionnie;
    cout << index_B_D_P +1 << "\n";
    cout << index_M_D_P +1<< "\n";
    cout << index_B_K_P +1<< "\n";
    cout << index_M_K_P +1<< "\n";
    cout << vse_prodazji << "\n";
    cout << vse_komissionnie << "\n";
    cout << vse_dengi << "\n";


}

int ToDigit(char letter) {
    int levels[] = {15, 14, 13, 12, 11, 10};
    map <char, int> symbol = {
                    {'F', 15},
                    {'E', 14},
                    {'D', 13},
                    {'C', 12},
                    {'B', 11},
                    {'A', 10}};
    char result = int();
    result += symbol[letter];
    return result;
}

string FromDigit(int letter) {
    string levels[] = { "F", "E", "D", "C", "B", "A" };
    map <int, string> symbol = {
                    {15, "F"},
                    {14, "E"},
                    {13, "D"},
                    {12, "C"},
                    {11, "B"},
                    {10, "A"} };
    string result = string();
    result += symbol[letter];
    return result;
}

int task_9() {
    string old_number, new_num;
    int old_osn, new_osn, des_num=0;
    cout << "ВВедите число" << "\n";
    cin >> old_number;
    cout << "ВВедите его основание" << "\n";
    cin >> old_osn;
    cout << "ВВедите новое основание" << "\n";
    cin >> new_osn;
    if (old_osn != 10) {
        for (int i = 0; i < old_number.length(); i++) { //привод к десятичной системе
            if (isdigit(old_number[i])) {
                int x = old_number[i] - '0'; // char в int
                if (x < old_osn)
                    des_num += x * pow(old_osn, old_number.length() - (i + 1));
                else {
                    cout << "oops" << "\n";
                    return -1;
                }
            }
            else {
                des_num += ToDigit(old_number[i]) * pow(old_osn, old_number.length() - (i + 1));
            }
        }
    }

    else 
        des_num = stoi(old_number);
    if (new_osn > 1) {
        while (des_num != 0) { //получение числа в новом основании
            if (des_num % new_osn < 10) {
    new_num += to_string(des_num % new_osn);
    des_num = des_num / new_osn;
}
else {
    new_num += FromDigit(des_num % new_osn);
    des_num = des_num / new_osn;
}

            

        }
        reverse(new_num.begin(), new_num.end());
        cout << new_num << "\n";
        return 0;
    }
    else {
        cout << "oops" << "\n";
        return -1;
    }
    
}

int main()
{
    SetConsoleCP(1251);
    setlocale(LC_ALL, "RUS");//i want to choose
    
}
