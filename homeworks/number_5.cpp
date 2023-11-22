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
    string a2, text,a = "qwertyisnotpassword";
    ofstream MyFile("HW5.txt");
    ofstream MyFile2("HW6.txt");
    if (MyFile.is_open()) {
        MyFile << a;
        MyFile.close();
    }
    ifstream cuca("HW5.txt");
    if (cuca.is_open()) {
        while (getline(cuca, text)) {
            // вывод текста из файла
            //cout << text << "\n";
            for (int i = 0; i <= text.length(); i++) {
                text[i] = tolower(text[i]);
                if (int(text[i]) < 122) {
                    a2 += to_string(int(a[i]) + 1);
                    //text[i] = "";
                }
                else {
                    a2 += to_string(97);
                }   
            }
        } 
    }
    if (MyFile2.is_open()) {
        MyFile2 << a2;
        MyFile2.close();
    }
    rename("HW5.txt", "HW6.txt");
    return;

}
