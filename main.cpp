#include <QtGui/QApplication>
#include "account.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    account w;
    w.show();

    return a.exec();
}
