```shell
sudo -u postgres -H -- psql -c "DROP DATABASE IF EXISTS telegram_bot;"
sudo -u postgres -H -- psql -c "DROP USER IF EXISTS telegram_bot_user;"
sudo -u postgres -H -- psql -c "DROP USER IF EXISTS flyway;"

sudo -u postgres -H -- psql -c "CREATE DATABASE telegram_bot;"
sudo -u postgres -H -- psql -c "CREATE USER telegram_bot_user WITH ENCRYPTED PASSWORD 'telegram_bot_password';"
sudo -u postgres -H -- psql -c "GRANT ALL PRIVILEGES ON DATABASE telegram_bot TO telegram_bot_user;"
sudo -u postgres -H -- psql -c "CREATE USER flyway WITH ENCRYPTED PASSWORD 'flyway';"
sudo -u postgres -H -- psql -c "ALTER USER flyway WITH SUPERUSER;"
```