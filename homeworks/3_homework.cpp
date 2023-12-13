void task_4() {
    cout << "Задача 4" << "\n";
    string a;
    char data[20];
    ofstream MyFile("HW2.txt");
    if (MyFile.is_open()) {
        MyFile << "I";
        MyFile << "789";
        MyFile << "love";
        MyFile << "2";
        MyFile << "I";
        MyFile.close();
    }

    ifstream cuca("HW2.txt");
    if (cuca.is_open()) {

        while (cuca.getline(data, 20)) {
            //cout << data << "\n";
            string wiwod;
            for (int i = 0; i < strlen(data); i++) {
                //    //cout << data[i] << "\n";
                
                if (isdigit(data[i])) {
                    wiwod += data[i];
                }
                else{
                    if (wiwod.length() != 0)
                        cout << wiwod << "\n";
                    wiwod = "";
                }
            }
        }
    }
    cuca.close();
}
