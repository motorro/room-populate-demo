/*
 * The MIT License (MIT)
 *
 * Copyright (c)  2020. Nikolai Kotchetkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import minimist, {ParsedArgs} from "minimist";
import {cities} from "./cities";
import * as path from "path"
import * as fs from "fs"

/**
 * DB schema path
 */
const SCHEMA_PATH = path.join(__dirname, "..", "android", "app", "schemas", "com.motorro.roompopulate.cities.CitiesDb");
/**
 * DB destination file
 */
const DST_PATH = path.join(__dirname, "..", "android", "app", "src", "main", "assets", "databases", "cities.db");
/**
 * DB version file
 */
const VERSION_PATH = path.join(__dirname, "..", "android", "app", "gradle.properties");
/**
 * DB version key
 */
const VERSION_KEY = "CITIES_DB_VERSION";

/**
 * Known arguments
 */
interface Args extends ParsedArgs {
    dataDir: string
}

/**
 * Increments database version which room uses to guess if new data is available
 * @param file Path to properties
 * @param key Key to set version to
 */
function incrementDatabaseVersion(file: string, key: string) {
    const versionRegex = new RegExp(`${key}\\s*=\\s*(\\d+)`, "gi");
    let contents = fs.readFileSync(file, "utf8");
    const matches = versionRegex.exec(contents);
    if (null === matches) {
        throw new Error("Did not find match in specified file");
    }

    const version = parseInt(matches[1]);
    const newVersion = version + 1;

    console.log("Current version: " + version + ". New version: " + newVersion);

    contents = contents.replace(versionRegex, `${key}=${newVersion}`);

    fs.writeFileSync(file, contents)
}

/**
 * City population
 */
exports.cities = async () => {
    const options: Args = minimist<Args>(
        process.argv.slice(2),
        {
            string: ["dataDir"],
            default: { dataDir: "_data"}
        }
    );

    // 1. Create database
    const pathToNewDb = await cities(SCHEMA_PATH, options.dataDir);
    // 2. Copy to assets
    fs.copyFileSync(pathToNewDb, DST_PATH);
    // 3. Increment version
    incrementDatabaseVersion(VERSION_PATH, VERSION_KEY);
};
