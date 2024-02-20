int massive(int n) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            int elem;
            cout << "ВВедите элемент" << "\n";
            cin >> elem;
            return elem;
        }
    }
}


void tsk_8() {
    int s;
    cout << "Огласите размер  квадратной матрицы" << "\n";
    cin>> s;
    if (s == 2) {
        int answer[2];
        int matrix[2][2];
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                matrix[i][j] = massive(s);
            }
        }
        answer[0] = matrix[1][0];
        answer[1] = matrix[0][1];
        for (int i = 0; i < 2; i++) {
            cout << answer[i]<<" ";
            }
    }
    if (s == 3) {
        int matrix[3][3];
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                matrix[i][j] = massive(s);
            }
        }
        int vk, s, nk;
        vk = matrix[1][0] + matrix[0][1];
        s = matrix[2][0] + matrix[1][1] + matrix[0][2];
        nk = matrix[2][1] + matrix[1][2];
        if (vk > s and vk > nk) {
            cout << matrix[1][0] << " " << matrix[0][1] << "\n";
        }
        if (nk > s and nk > vk) {
            cout << matrix[2][1] << " " << matrix[1][2] << "\n";
        }
        if (s > vk and s > nk) {
            cout << matrix[2][0] << " " << matrix[1][1] << " "<< matrix[0][2]<<"\n";
        }
    }
}
