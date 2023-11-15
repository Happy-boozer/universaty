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

void RESHETO() {
    int n=0, m = 0, d=0;
    cin >> n;
    int* arr = new int(n + 3);
    
    for (int i = 2; i < n + 3;i++) {
        arr[i] = i;
    }
    //cout << arr[n] << "\n";

    for (int j = 2; j < n; j++) {
        //cout << arr[j] << "\n";
        for (int k = 3; k < n++; k++) {
            //cout << arr[k] << "\n";
        
            if (arr[k] % arr[j] == 0 and arr[k]!= arr[j] and arr[k]!=0) {
                cout << arr[k]<< arr[j] << "\n";
                arr[k] = 0;
           }
        }
    }
    //cout << arr << "\n";
   /*for (int r = 2; r < n+2; r++)
        cout << arr[r] << "\n";*/
    return;

}
