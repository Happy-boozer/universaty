void task_1() {
    float h, r, R, p, V, S, l;
    //V = 1/3 ph(R^2 + Rr + r^2)
    // S = p(R^2 + (R + r)l + r^2)
    p = M_PI;
    cout << "Введите пожалуйста высоту нашего усечённого конуса" << "\n";
    cin >> h;
    cout << "Введите пожалуйста радиус основания нашего усечённого конуса" << "\n";
    cin >> R;
    cout << "Введите пожалуйста радиус верхний нашего усечённого конуса" << "\n";
    cin >> r;
    if (h > 0 and R > 0 and r > 0) {
        l = pow(h * h + pow((R - r), 2), 0.5);
        V = 1.0 / 3.0 * p * h * (R * R + R * r + r * r);
        S = p * (R * R + (R + r) * l + r * r);
        cout << "Посмотрите я посчиталь обЪём" << "\n";
        cout << V << "\n";
        cout << "И полную поверхность посчиталь" << "\n";
        cout << S << "\n";
    }
    else
        cout << "КУ-ку?" << "\n";
    
}

void task_2() {
    cout << "задача номер 2" << "\n";
    float a, x;
    double answer;
    cout << "Введите число a" << "\n";
    cin >> a;
    cout << "Введите пложалуйста число X" << "\n";
    cin >> x;
    if (abs(x) < 1 and abs(x)       != 0) {
        answer = a * log(abs(x));
        cout << answer << "\n";
    }
    if (abs(x) >= 1) {
        if (pow(x, 2) > a) {
            cout << "ненада" << "\n";
        }
        else {

            answer = pow((a - x * x), 0.5);
            cout << answer << "\n";
        }
    }
}

void task_3() {
    cout << "задача номер 3" << "\n";
    float b, y, answer, w;
    cout << "Введите пложалуйста число X" << "\n";
    cin >> w;
    cout << "Введите пложалуйста число b" << "\n";
    cin >> b;
    cout << "Введите пложалуйста число y" << "\n";
    cin >> y;
    if (((b - y) > 0) and ((b - w) >= 0)) {
        answer = log(b - y) * pow((b - w), 0.5);
        cout << answer << "\n";
    }
    else
        cout << "не буду я это считать ";

    
}
