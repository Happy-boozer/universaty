#include <time.h>
void task_1212() {

    int n;
    float s=0, r=0, t=0;
    cout << "Input parametr" << "\n";
    cin >> n;
    clock_t start = clock();
    for (float i = 0; i <=1 ; i+=0.1) {
        s += i;
    }
    for (int j = 2; j < n; j++) {
        r += j;
    }
    t = (1 + r) / (1 + s);
    clock_t end = clock();
    double seconds = (double)(end - start) / CLOCKS_PER_SEC;
    cout << "result:"<<t << "\n";
    cout << seconds << "\n";
}
