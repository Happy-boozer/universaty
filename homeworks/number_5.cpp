#include <iostream>
#include<Windows.h>
#include <fstream>
#include <string>
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

void RESHETO(int n) {
    int n = 0, m = 0;
//cin >> n;
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

void task_3_8() {
    string text, a2, a="ASwfhbrkghertgurtnbrbirwjijbiwjrtithblrtjhrktjhiorjgoqerjgoprjgpojrgpowjhpowrjhpowrtjhopwrjtopqrjh';lrthj";
    ofstream MyFile("HW2.txt");
    if (MyFile.is_open()) {
        MyFile << a;
        MyFile.close();
    }
    ifstream cuca("HW2.txt");
    if (cuca.is_open()) {
        while (getline(cuca, text)) {
            // вывод текста из файла
            //cout << text << "\n";
            for (int i = 0; i <= text.length(); i++) {
                //cout << int(a[i]) << "\n";
                a2 += to_string(int(a[i]));
            }
        }
    }
    //cout << a2 << "\n";
    cuca.close();
    return;
}

void task_3_32() {
    string a2, text, a = "Iloveyou";
    ofstream MyFile("HW5.txt");
    if (MyFile.is_open()) {
        MyFile << a;
        MyFile.close();
    }
    ifstream cuca("HW5.txt");
    if (cuca.is_open()) {
        while (getline(cuca, text)) {
            // вывод текста из файла
            //cout << text << "\n";
            for (int i = 0; i < text.length(); i++) {
                text[i] = tolower(text[i]);
                if (int(text[i]) < 122) {
                    a2 += char (int(a[i]) + 1);
                    //text[i] = "";
                }
                else {
                    a2 += char(97);
                }
            }
        }
    }
    cout << a2 << "\n";
    return;

}

void task_4_61() {
    int* arr;
    int n,min, max;
    cin >> n;
    arr = new int[n];
    for (int i = 0; i < n; ++i)
        cin >> arr[i];
    sort(arr, arr + n);
    int c = 0;
    for (int tmp = 1; tmp <= arr[0]; ++tmp)
    {
        bool b = 0;
        
        for (int i = 0; i < n; ++i)
            if (arr[i] % tmp != 0)
            {
                b = 1;
               
                break;
            }
        if (!b) {
            //cout << tmp << "\n";
            max = tmp;
            c += 1;
            if (c == 2) {
                min = tmp;
            }
        }
        
    }
    cout << max << "\n";
    cout << min << "\n";
   
}

void task_5_9() {
    int C[12];//Финальный массив
    ofstream MyFile("A.txt");
    if (MyFile.is_open()) {
        MyFile << "12"<<"\n";
        MyFile << "789"<<"\n";
        MyFile << "89"<<"\n";
        MyFile << "77"<<"\n";
        MyFile.close();
    } 
    string Q[6]; // мвссив для считывания
    ifstream cuca("A.txt");
    if (cuca.is_open()) {
        for (int k = 0; k < 6; k++) {
            string a = "";
            getline(cuca, a);
            string wiwod;
            for (int i = 0; i < a.length(); i++) {
                if (isdigit(a[i])) {
                    wiwod += a[i];
                }
                Q[k] = wiwod;
            }
        }
    }

    ofstream Myfile("B.txt");
    if (Myfile.is_open()) {
        Myfile << "16"<<"\n";
        Myfile << "86"<<"\n";
        Myfile << "9"<<"\n";
        Myfile << "5"<<"\n";
        Myfile.close();
    }
    string R[6];
    ifstream cuca1("B.txt");
    if (cuca1.is_open()) {
        for (int k = 0; k < 6; k++) {
            string a = "";
            getline(cuca1, a);
            string wiwod;
            for (int i = 0; i < a.length(); i++) {
                if (isdigit(a[i])) {
                    wiwod += a[i];
                }
                R[k] = wiwod;
            }
        }
    }

    for (int s = 0; s < 12; s++) {
        C[s] = 0;
    }
    for (int j = 0;j < 6; j++) {
        if (Q[j].length() > 0)
            C[j] = stoi(Q[j]);
    }
    for (int t = 6; t < 12; t++) {
        if (R[t-6].length() > 0)
            C[t] = stoi(R[t - 6]);
    }

    for (int g = 0; g < 12; g++) {
        for (int d = 0; d < 11; d++) {
            if (C[d] > C[d+1]) {
                int temp = C[d];
                C[d] = C[d+1];
                C[d+1] = temp;
            }

        }
    }
    //for (int s = 0; s < 12; s++) {
    //    cout << C[s] << "\n";
    //}
    ofstream Mfile("C.txt");
    if (Mfile.is_open()) {
        for (int i = 0; i < 12; i++) {
            if (C[i] != 0)
                Mfile << C[i] << "\n";
        }
    }
    Mfile.close();
}
