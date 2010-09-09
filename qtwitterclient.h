/*
Copyright 2010 Soluvas <http://www.soluvas.com>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library.  If not, see <http://www.gnu.org/licenses/>.
*/

#ifndef QTWITTERCLIENT_H
#define QTWITTERCLIENT_H

#include <QObject>
#include <QString>
#include <QNetworkAccessManager>
#include <QNetworkReply>

class QTwitterClient : public QObject
{
    Q_OBJECT
private:
    QString m_login;
    QString m_password;
    QNetworkAccessManager *m_nam;
private slots:
    void replyFinished(QNetworkReply *reply);
    void replyError(QNetworkReply::NetworkError code);
public:
    QTwitterClient(QObject *parent);
    QString login() const;
    QString password() const;
    void setLogin(QString login);
    void setPassword(QString password);
    void tweet(const QString& message);
    void tweetGeo(const QString& message, double longitude, double latitude);
signals:
    void finishedSuccess();
    void finishedError(QNetworkReply::NetworkError, QString errorString);
    void failed(QNetworkReply::NetworkError, QString errorString);
};

#endif // TWITTERCLIENT_H
