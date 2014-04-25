rm cookies.txt
read -p "email: " EMAIL
read -p "password: " PASSWRD
wget --user-agent=Mozilla/5.0 --save-cookies cookies.txt --keep-session-cookies --post-data "email=$EMAIL&pass=$PASSWRD" --no-check-certificate https://www.facebook.com/login -e use_proxy=yes -e http_proxy=127.0.0.1:8080
wget --load-cookies cookies.txt \
     -p https://www.facebook.com/naveen.mn.9
