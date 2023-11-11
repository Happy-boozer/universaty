#include <iostream>
#include<cmath>
#include<Windows.h>

using namespace std;

void task_1() {
    cout << "Задание1" << "\n";
    cout << "Александр" << endl;
    return;
}

void task_2() {
    float a, b;
    cout << "Задание2" << "\n";
    cin >> a;
    cin >> b;

    cout << "Summ" << a + b << "\n";
    cout << "Diff" << a - b << "\n";
    cout << "Multi" << a * b << endl;
    if (b != 0) {
        float x = a / b;
        cout << "Divisiom" << x << "\n";
    }
    else {
        cout << "mmmmmmm" << endl;
    }
    return;
}

void task_3() {
    float b, c;
    cout << "Задание3" << "\n";
    cin >> b;
    cin >> c;
    if (b != 0)
        cout << -c / b << endl;
    else
    if (b == 0 and c != 0) {
        cout << "бесконечное число решений" << "\n";
    }
    else
    if (b != 0 and c == 0)
        cout << 0 << "\n";
}
