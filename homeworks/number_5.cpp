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
    for (int i = 2; i <= pow(n, 0.5);i++) {
        if (n % i == 0) {
            return false;
        }
    }
    return true;
}

void RR() {
    int n, c = 0;
    cin >> n;
    for (int i = 2; i <= n; i++) {
        if (RESHETO(i))
            c += 1;
    }
    cout << c << "\n";
    return;
}
