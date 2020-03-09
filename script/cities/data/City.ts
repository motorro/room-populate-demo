/**
 * A city from city.list.json
 */
export interface City {
    readonly id: number
    readonly name: string
    readonly state: string
    readonly country: string
    readonly coord: Coord
}

/**
 * City coordinate
 */
export interface Coord {
    readonly lat: number
    readonly lon: number
}