import {Database, Statement} from "sqlite3";
import {City} from "./data/City";

/**
 * Populates DB with data
 */
export async function populate(db: Database, cities: Array<City>): Promise<void> {
    console.log("Populating Cities database...");
    await populateCities(db, cities);
}

/**
 * Sample data validation
 * @param city
 */
function isValidData(city: City): Boolean {
    return city.name.length > 0 && city.country.length > 0;
}

/**
 * Populates Cities
 */
 async function populateCities(db: Database, cities: Array<City>) {
    console.log("Populating Cities table...");
    let validRecords = 0;
    let invalidRecords: Array<number> = [];

    const stmt = await new Promise<Statement> ((resolve, reject) => {
        db.prepare(
            "REPLACE INTO cities VALUES (?,?,?,?,?,?)",
            function(this: Statement, err: Error) {
                if(null != err) {
                    reject(err);
                } else {
                    resolve(this);
                }
            }
        );
    });

    for (const city of cities) {
        if (false == isValidData(city)) {
            console.warn(`Invalid city: ${city.id}`);
            invalidRecords.push(city.id);
            continue;
        }
        await new Promise<void>((resolve, reject) => {
            stmt.run(
                [
                    city.id,
                    city.name,
                    city.state.length > 0 ? city.state : null,
                    city.country,
                    city.coord.lat,
                    city.coord.lon
                ],
                function (err: Error) {
                    if (null != err) {
                        reject(err);
                    } else {
                        validRecords++;
                        resolve();
                    }
                }
            );
        })
    }
    console.log("Cities populated.");
    console.log(`Valid records: ${validRecords}`);
    console.log(`Invalid records (${invalidRecords.length}):`, invalidRecords);
    stmt.finalize();
}

