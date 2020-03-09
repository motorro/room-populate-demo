## City database import

Cities are shipped with application in a SQLite database:
1. Copy `city.list.json` from server. Default is `_data` directory here. 
2. Run the following command to import cities data from file to database:
```
  npx gulp cities --dataDir=[path_to_source_files]
```
This command will:
1. Create the latest schema from Room definition.
2. Import data to SQLite database.
3. Put it to `app/assets/databases` folder.