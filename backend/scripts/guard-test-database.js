const database = process.env.DB_DATABASE;
if (database !== 'bolclick_test') {
  console.error(
    'Refusing to run database-backed e2e tests: set DB_DATABASE=bolclick_test. The application database (mall_db) is never a test target.',
  );
  process.exit(1);
}
if (!process.env.DB_HOST || !process.env.DB_USERNAME) {
  console.error('Set DB_HOST and DB_USERNAME for the isolated test database.');
  process.exit(1);
}
if (process.env.NODE_ENV !== 'test') {
  console.error('Set NODE_ENV=test before running database-backed e2e tests.');
  process.exit(1);
}
