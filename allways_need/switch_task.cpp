int sost = 10;
while (sost != 0){
    cout << "ВВедите пожалуйста номер задачи от 1 до 9, если хотите закончить введите 0" << "\n";
    cin >> sost;
    switch (sost) {
    case 1:
        task_1();
        break;
    case 2:
        task_2();
        break;
    case 3:
        task_3();
        break;
    case 4:
        task_4();
        break;
    }
