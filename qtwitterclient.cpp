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

#include "qtwitterclient.h"
#include <QtNetwork>
#include <QUrl>

QTwitterClient::QTwitterClient(QObject *parent)
    : QObject(parent)
{
    qRegisterMetaType<QNetworkReply::NetworkError>("QNetworkReply::NetworkError");
    m_nam = new QNetworkAccessManager(this);
    connect(m_nam, SIGNAL( finished(QNetworkReply*) ),
            SLOT( replyFinished(QNetworkReply*)) );
}

QString QTwitterClient::login() const {
    return m_login;
}

QString QTwitterClient::password() const {
    return m_password;
}

void QTwitterClient::setLogin(QString login) {
    m_login = login;
}

void QTwitterClient::setPassword(QString password) {
    m_password = password;
}

void QTwitterClient::tweet(const QString &message) {
    QNetworkRequest request;
    request.setUrl(QUrl("http://twitter.com/statuses/update.json"));
    QString credentials = login() + ":" + password();
    credentials = "Basic " + credentials.toAscii().toBase64();
    request.setRawHeader("Authorization", credentials.toAscii());
    QByteArray data = "status=" + QUrl::toPercentEncoding(message);
    QNetworkReply *reply = m_nam->post(request, data);
    connect(reply, SIGNAL(error(QNetworkReply::NetworkError)),
        SLOT(replyError(QNetworkReply::NetworkError)));
}

void QTwitterClient::tweetGeo(const QString& message, double longitude, double latitude) {
    QNetworkRequest request;
    request.setUrl(QUrl("http://twitter.com/statuses/update.json"));
    QString credentials = login() + ":" + password();
    credentials = "Basic " + credentials.toAscii().toBase64();
    request.setRawHeader("Authorization", credentials.toAscii());
    QByteArray data = "status=" + QUrl::toPercentEncoding(message) +
                      "&lat=" + QUrl::toPercentEncoding(QString::number(latitude, 'f', 8)) +
                      "&long=" + QUrl::toPercentEncoding(QString::number(longitude, 'f', 8));
    QNetworkReply *reply = m_nam->post(request, data);
    connect(reply, SIGNAL(error(QNetworkReply::NetworkError)),
        SLOT(replyError(QNetworkReply::NetworkError)));
}

void QTwitterClient::replyFinished(QNetworkReply *reply) {
    if (reply->error() == QNetworkReply::NoError) {
        qDebug() << "Finished successfully";
        finishedSuccess();
    } else {
        qDebug() << "Finished with Error:" << reply->error() << reply->errorString();
        finishedError(reply->error(), reply->errorString());
    }
    reply->deleteLater();
}

void QTwitterClient::replyError(QNetworkReply::NetworkError code) {
    QNetworkReply *reply = static_cast<QNetworkReply *>(sender());
    QString errorString(reply->errorString());
    qDebug() << "Premature Error:" << code << errorString;
    failed(code, errorString);
}
