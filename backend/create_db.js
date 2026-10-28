const { Client } = require('pg');

const client = new Client({
  host: process.env.DB_HOST || 'localhost',
  port: Number(process.env.DB_PORT) || 5432,
  user: process.env.DB_USERNAME || 'postgres',
  password: process.env.DB_PASSWORD || '',
  database: 'postgres'
});

client.connect()
  .then(() => client.query('CREATE DATABASE mall_db'))
  .then(() => console.log('Database created'))
  .catch((err) => console.log('Notice or error: ' + err.message))
  .finally(() => client.end());
