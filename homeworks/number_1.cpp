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

void task_4() {
    float m, r, t, d, x1, x2, x;
    cout << "Задание4" << "\n";
    cout << "Ввкдите коэфф a" << "\n";
    cin >> m;
    cout << "Ввкдите коэфф b" << "\n";
    cin >> r;
    cout << "Ввкдите коэфф c" << "\n";
    cin >> t;

    if (m != 0) {
        d = r * r - 4 * m * t;
        if (d > 0) {
            x1 = (-r + pow(d, 0.5)) / (2 * m);
            x2 = (-r - pow(d, 0.5)) / (2 * m);
            cout << "x1==" << x1 << endl;
            cout << "x2==" << x2 << endl;
        }
        if (d == 0) {
            x = -r / 2 * m;
            cout << x << endl;
        }
        if (d < 0) {
            cout << "решений нет" << endl;
        }
    }
    else {
        if (r != 0 and m != 0) {
            cout << "x==" << -t / r << endl;
        }
        if (r == 0 and t != 0) {
            cout << "решений нет" << endl;
        }
        if (r == 0 and t == 0) {
            cout << "любое x" << endl;
        }
        else {

        }

    }
}
