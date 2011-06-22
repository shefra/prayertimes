/********************************************************************************
** Form generated from reading UI file 'accountDialog.ui'
**
** Created: Tue Sep 7 05:12:00 2010
**      by: Qt User Interface Compiler version 4.6.2
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_ACCOUNTDIALOG_H
#define UI_ACCOUNTDIALOG_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QDialog>
#include <QtGui/QHBoxLayout>
#include <QtGui/QHeaderView>
#include <QtGui/QLabel>
#include <QtGui/QLineEdit>
#include <QtGui/QPushButton>
#include <QtGui/QVBoxLayout>

QT_BEGIN_NAMESPACE

class Ui_account
{
public:
    QVBoxLayout *verticalLayout_2;
    QVBoxLayout *verticalLayout;
    QLabel *label;
    QHBoxLayout *horizontalLayout;
    QLineEdit *lineEdit;
    QPushButton *enterButton;
    QPushButton *SynchronizeButton;

    void setupUi(QDialog *account)
    {
        if (account->objectName().isEmpty())
            account->setObjectName(QString::fromUtf8("account"));
        account->resize(265, 111);
        verticalLayout_2 = new QVBoxLayout(account);
        verticalLayout_2->setSpacing(6);
        verticalLayout_2->setContentsMargins(11, 11, 11, 11);
        verticalLayout_2->setObjectName(QString::fromUtf8("verticalLayout_2"));
        verticalLayout = new QVBoxLayout();
        verticalLayout->setSpacing(6);
        verticalLayout->setObjectName(QString::fromUtf8("verticalLayout"));
        label = new QLabel(account);
        label->setObjectName(QString::fromUtf8("label"));
        label->setTextFormat(Qt::RichText);

        verticalLayout->addWidget(label);

        horizontalLayout = new QHBoxLayout();
        horizontalLayout->setSpacing(6);
        horizontalLayout->setObjectName(QString::fromUtf8("horizontalLayout"));
        lineEdit = new QLineEdit(account);
        lineEdit->setObjectName(QString::fromUtf8("lineEdit"));

        horizontalLayout->addWidget(lineEdit);

        enterButton = new QPushButton(account);
        enterButton->setObjectName(QString::fromUtf8("enterButton"));

        horizontalLayout->addWidget(enterButton);


        verticalLayout->addLayout(horizontalLayout);


        verticalLayout_2->addLayout(verticalLayout);

        SynchronizeButton = new QPushButton(account);
        SynchronizeButton->setObjectName(QString::fromUtf8("SynchronizeButton"));

        verticalLayout_2->addWidget(SynchronizeButton);


        retranslateUi(account);

        QMetaObject::connectSlotsByName(account);
    } // setupUi

    void retranslateUi(QDialog *account)
    {
        account->setWindowTitle(QApplication::translate("account", "account", 0, QApplication::UnicodeUTF8));
        label->setText(QApplication::translate("account", "\330\243\330\257\330\256\331\204 pin", 0, QApplication::UnicodeUTF8));
        enterButton->setText(QApplication::translate("account", "\330\243\330\257\330\256\331\204", 0, QApplication::UnicodeUTF8));
        SynchronizeButton->setText(QApplication::translate("account", "\330\247\330\263\331\205\330\255\331\204\331\212 \330\243\331\206 \330\247\330\252\330\262\330\247\331\205\331\206 \331\205\330\271 \330\255\330\263\330\247\330\250\331\203", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class account: public Ui_account {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_ACCOUNTDIALOG_H
