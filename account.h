#ifndef ACCOUNT_H
#define ACCOUNT_H

#include <QtGui/QDialog>
namespace Ui {
    class account;
}

namespace QOAuth {
    class Interface;
}

class account : public QDialog
{
    Q_OBJECT

public:
    explicit account(QWidget *parent = 0);
    ~account();
    QByteArray getScreenName() ;
    QByteArray getOAuthKey() ;

private:
    Ui::account *ui;
    QOAuth::Interface *qoauth;

    QByteArray screenName;
    QByteArray token;
    QByteArray tokenSecret;
private slots:
    void  SynchronizeWithAccount();
    void openBroweser();
    void allowEnterButton();

};

#endif // ACCOUNT_H
