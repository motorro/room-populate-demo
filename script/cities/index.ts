import * as Path from "path";
import * as fs from "fs";
import * as os from "os";
import {Guid} from "guid-typescript";
import {Database} from "sqlite3";
import {populate} from "room-populate";
import {City} from "./data/City"
import * as citiesDb from "./citiesDb"
import {duration} from "moment";

/**
 * Cities file
 */
const FILE_CITIES = "city.list.json";

/**
 * Takes cities source files and populates SQLite database
 * @param schemaPath Where the Room schemas are put
 * @param dataDir Where the data is put
 * @return A promise for created database
 */
export async function cities(schemaPath: string, dataDir: string): Promise<string> {
    const schemaFile = await findLatestSchema(schemaPath);
    const timeStarted = Date.now();

    const name: string = Guid.create().toString();
    const path = Path.join(os.tmpdir(), `${name}.db`);
    const db = new Database(path);

    await populate(
        Path.join(schemaPath, schemaFile),
        db,
        async function(this: Database): Promise<void> {
            await citiesDb.populate(
                db,
                await readJsonData<Array<City>>(Path.join(dataDir, FILE_CITIES))
            );
        }
    );

    db.close();

    console.log("Total time: ", duration(Date.now() - timeStarted).humanize());
    return path;
}

/**
 * Finds latest room schema file
 * @param schemaPath
 */
async function findLatestSchema(schemaPath: string): Promise<string> {
    return new Promise<string>(function (resolve, reject) {
        console.log ("Getting latest schema...");
        fs.readdir(schemaPath, { encoding: "utf8", withFileTypes: true }, function (err, files) {
            if (err) {
                return reject(err);
            }
            const nameRegExp: RegExp = /(\d+)\.json/i;
            let latestVersion: number = 0;
            let latestVersionFile: undefined|fs.Dirent = undefined;
            for (let i = 0, l = files.length; i < l; ++i) {
                const file = files[i];
                if (file.isFile()) {
                    const check = nameRegExp.exec(file.name);
                    if (check) {
                        const version = parseInt(check[1]);
                        if (version > latestVersion) {
                            latestVersion = version;
                            latestVersionFile = file;
                        }
                    }
                }
            }
            if (undefined != latestVersionFile) {
                console.log("Found latest version: ", latestVersion);
                resolve (latestVersionFile.name);
            } else {
                reject(new Error("No schema found in specified path"))
            }
        });
    });
}

/**
 * Reads JSON data from file
 * @param file File to read
 */
async function readJsonData<T>(file: string): Promise<T> {
    return new Promise<T>(function (resolve, reject) {
        fs.readFile(file, { encoding: "utf8"}, function (err, contents) {
            if (err) {
                reject(err);
            } else {
                try {
                    resolve(JSON.parse(contents) as T);
                } catch (err) {
                    reject (err);
                }
            }
        });
    });
}