#include "account.h"
#include "ui_accountDialog.h"
#include <QtGui>
#include <QtOAuth/QtOAuth>
#include <QMessageBox>
#include <QDesktopServices>
#include <QUrl>
account::account(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::account)
{
    ui->setupUi(this);
    QTextCodec::setCodecForCStrings(QTextCodec::codecForName("UTF-8"));
    ui->enterButton->setEnabled(false);
    qoauth = new QOAuth::Interface(this);
    qoauth->setConsumerKey("sDX0nYM4dyb91z7HqTSLzw");
    qoauth->setConsumerSecret("KGdme9ZNBiLY7mI19U4yo4Hrv4duWBdONkQdwc");
    connect(ui->SynchronizeButton,SIGNAL(clicked()),this,SLOT(openBroweser()));


}

account::~account()
{
    delete ui;
}

void account::openBroweser()
{
    QOAuth::ParamMap requestToken = qoauth->requestToken( "https://api.twitter.com/oauth/request_token", QOAuth::GET, QOAuth::HMAC_SHA1 );
    if (qoauth->error() != QOAuth::NoError)
    {
        QMessageBox::about(this,"ERROR","ERROR");
    }
    token = requestToken.value(QOAuth::tokenParameterName());
    tokenSecret = requestToken.value(QOAuth::tokenSecretParameterName());
    QString url = "https://api.twitter.com/oauth/authorize";
    url.append( "?" );
    url.append( "&" + QOAuth::tokenParameterName() + "=" + token );
    url.append( "&oauth_callback=oob" );
    QDesktopServices::openUrl(QUrl(url));
    connect(ui->lineEdit,SIGNAL(textChanged(QString)),this,SLOT(allowEnterButton()));
    connect(ui->enterButton,SIGNAL(clicked()),this,SLOT(SynchronizeWithAccount()));
}

void account::SynchronizeWithAccount()
{
    QOAuth::ParamMap arg;
    arg.insert("oauth_verifier",ui->lineEdit->text().toAscii());
    QOAuth::ParamMap accessToken = qoauth->accessToken("https://api.twitter.com/oauth/access_token",QOAuth::POST,token,tokenSecret,QOAuth::HMAC_SHA1,arg);
    if(qoauth->error() != QOAuth::NoError)
    {
        QMessageBox::about(this,"خطأ","يبدوا أن هناك خطأ في رمز الpin او في الاتصال بالنت");
    }
    screenName = accessToken.value( "screen_name" );
    token = accessToken.value( QOAuth::tokenParameterName() );
    tokenSecret = accessToken.value( QOAuth::tokenSecretParameterName() );
    QMessageBox::about(this,"d",screenName);
    accept();
}

QByteArray account::getScreenName()
{
    return screenName;
}

QByteArray account::getOAuthKey()
{
    return token + "&" + tokenSecret;
}

void account::allowEnterButton()
{
    if(ui->lineEdit->text().length() >= 6)
        ui->enterButton->setEnabled( true );
    else
        ui->enterButton->setEnabled( false );
}
