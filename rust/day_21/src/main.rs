use std::collections::HashSet;
use std::fs;

const START_STRING: &str = ".#./..#/###";

fn main() {
    let start_pattern = parse_pixels(START_STRING);
    // println!("{:#?}", start_pattern);

    let rules = read_input("input.txt");

    // let example = parse_pixels("....../....../....../....../....../....../....../....../......");
    let example = parse_pixels("#....#/....../....../#....#");
    let example = example.to_two_by_twos();
    println!("{:#?}", example);

    // let matrix = &rules[50].before;
    // println!("{:#?}", matrix);
    // let rotated = rotate(matrix);
    // println!("{:#?}", rotated);
}

#[test]
fn example_test() {
    let rules = parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
    let example = parse_pixels(START_STRING);
    let expected = parse_pixels("#..#/..../..../#..#");

    // let expected = parse_pixels("##.##./#..#../....../##.##./#..#../......");

    let enhanced = example.enhance(rules);
    assert_eq!(*enhanced, expected);
}

fn read_input(filename: &str) -> Vec<Rule> {
    let input = fs::read_to_string(filename).unwrap();
    parse_rules(&input)
}

fn parse_rules(rules: &str) -> Vec<Rule> {
    rules.lines().map(|line| Rule::from_string(line)).collect()
}

type Pixels = Vec<Vec<Pixel>>;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
enum Pixel {
    On,
    Off,
}

impl Pixel {
    // TODO: make this return result
    fn from_char(c: char) -> Pixel {
        match c {
            '#' => Pixel::On,
            '.' => Pixel::Off,
            _ => panic!("tried to parse non-pixel: {:?}", c),
        }
    }
}

fn parse_pixels(line: &str) -> Pixels {
    line.split('/')
        .map(|row| row.chars().map(|c| Pixel::from_char(c)).collect())
        .collect()
}

#[derive(Debug)]
struct Rule {
    before: Pixels,
    after: Pixels,
    matches: HashSet<Pixels>,
}

trait Rotatable {
    fn rotate_right(&self) -> Self;
    fn rotate_left(&self) -> Self;
    fn rotate_180(&self) -> Self;
}

impl Rotatable for Pixels {
    fn rotate_right(&self) -> Pixels {
        let size = self.len();
        let mut rotated = vec![vec![Pixel::Off; size]; size];

        // // TODO: why did we need this?
        // for _row in 0..size {
        //     let col = Vec::with_capacity(size);
        //     rotated.push(col);
        // }

        // thanks SO csharp guy
        for row in 0..size {
            for col in 0..size {
                rotated[row][col] = self[size - col - 1][row];
            }
        }

        rotated
    }

    fn rotate_left(&self) -> Pixels {
        self.rotate_right().rotate_right().rotate_right()
    }

    fn rotate_180(&self) -> Pixels {
        self.iter().cloned().rev().collect()
    }
}

trait Enhanceable {
    fn enhance(&self, rules: Vec<Rule>) -> Pixels;
    fn to_two_by_twos(&self) -> Vec<Vec<Pixels>>;
    fn to_three_by_threes(&self) -> Vec<Vec<Pixels>>;
}

impl Enhanceable for Pixels {
    fn enhance(&self, rules: Vec<Rule>) -> Self {
        if self.len() % 2 == 0 {
            // even, break into 2x2 and replace each with rules
            let twos = self.to_two_by_twos();
            // convert each two by two into a pixels
            let merged: Pixels = merge_two_by_twos(twos);
            // replace each pixels using apply_rules call
            let pixels = apply_rules(rules, merged);
            pixels.unwrap()
        } else {
            // assume multiple of 3, break into 3x3 and replace each with rules
            let threes = self.to_three_by_threes();
            let merged: Pixels = merge_three_by_threes(threes);
            let pixels = apply_rules(rules, merged);
            pixels.unwrap()
        }
    }

    fn to_three_by_threes(&self) -> Vec<Vec<Pixels>> {
        self.chunks(3)
            .map(|three_rows| {
                let three_rows_of_triples = three_rows
                    .into_iter()
                    .map(|row: &Vec<Pixel>| row.chunks(3).collect::<Vec<_>>())
                    .collect::<Vec<_>>();
                let row1 = &three_rows_of_triples[0];
                let row2 = &three_rows_of_triples[1];
                let row3 = &three_rows_of_triples[2];

                // TODO: does this rotate or break?
                let zipper = row1
                    .iter()
                    .cloned()
                    .zip(row2.iter().cloned().zip(row3.iter().cloned()));

                zipper
                    .map(|(triple1, (triple2, triple3))| {
                        vec![triple1.into(), triple2.into(), triple3.into()]
                    })
                    .collect::<Vec<_>>()
            })
            .collect()
    }

    fn to_two_by_twos(&self) -> Vec<Vec<Pixels>> {
        self.chunks(2)
            .map(|two_rows| {
                let two_rows_of_pairs = two_rows
                    .into_iter()
                    .map(|row: &Vec<Pixel>| row.chunks(2).collect::<Vec<_>>())
                    .collect::<Vec<_>>();

                let row1 = &two_rows_of_pairs[0];
                let row2 = &two_rows_of_pairs[1];

                // much clone wow
                let zipper = row1.iter().cloned().zip(row2.iter().cloned());
                // this rotates the 2x2s, which should be fine
                zipper
                    .map(|(pair1, pair2)| vec![pair1.into(), pair2.into()])
                    .collect::<Vec<_>>()
            })
            .collect()
    }
}

fn build_rule_match_set(pixels: &Pixels) -> HashSet<Pixels> {
    let add_all_rotations = |mut matches: HashSet<Pixels>, ps: &Pixels| {
        matches.insert(ps.clone());
        matches.insert(ps.rotate_right());
        matches.insert(ps.rotate_left());
        matches.insert(ps.rotate_180());
        matches
    };

    let matches = add_all_rotations(HashSet::new(), pixels);

    let flipped: Pixels = pixels
        .iter()
        .map(|v| v.iter().cloned().rev().collect::<Vec<Pixel>>())
        .collect();

    let matches = add_all_rotations(matches, &flipped);

    matches
}

impl Rule {
    fn from_string(line: &str) -> Rule {
        let tokens: Vec<&str> = line.split(" => ").collect();
        let before = parse_pixels(tokens[0]);
        let matches = build_rule_match_set(&before);
        let after = parse_pixels(tokens[1]);

        Rule {
            before,
            matches,
            after,
        }
    }

    fn matches(&self, pixels: &Pixels) -> bool {
        self.matches.contains(pixels)
    }
}

fn apply_rules(rules: Vec<Rule>, pixels: Pixels) -> Option<Pixels> {
    rules
        .iter()
        .find(|rule| rule.matches(&pixels))
        .map(|rule| rule.after.clone())
}

fn merge_two_by_twos(two_by_twos: Vec<Vec<Pixels>>) -> Pixels {
    let mut merged: Pixels = Vec::new();
    for twos_row in two_by_twos.iter() {
        let mut first_row: Vec<Pixel> = Vec::new();
        let mut second_row: Vec<Pixel> = Vec::new();
        for two_by_two in twos_row.iter() {
            first_row.extend(&two_by_two[0]);
            second_row.extend(&two_by_two[1]);
        }
        merged.push(first_row);
        merged.push(second_row);
    }

    merged
}

fn merge_three_by_threes(three_by_threes: Vec<Vec<Pixels>>) -> Pixels {
    let mut merged: Pixels = Vec::new();
    for threes_row in three_by_threes.iter() {
        let mut first_row: Vec<Pixel> = Vec::new();
        let mut second_row: Vec<Pixel> = Vec::new();
        let mut third_row: Vec<Pixel> = Vec::new();
        for three_by_three in threes_row.iter() {
            first_row.extend(&three_by_three[0]);
            second_row.extend(&three_by_three[1]);
            third_row.extend(&three_by_three[2]);
        }
        merged.push(first_row);
        merged.push(second_row);
        merged.push(third_row);
    }

    merged
}
