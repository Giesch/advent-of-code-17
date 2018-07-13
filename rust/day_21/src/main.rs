#![feature(iterator_flatten)]

use std::collections::HashSet;
use std::fs;

const START_STRING: &str = ".#./..#/###";

fn main() {
    let start_pattern = parse_pixels(START_STRING);
    let rules = read_input("input.txt");
    let solution = solve(start_pattern, &rules, 5);
    println!("{}", solution);
}

fn solve(start_pattern: Pixels, rules: &[Rule], iterations: u32) -> usize {
    let mut current = start_pattern;
    for _ in 0..iterations {
        current = current.enhance(rules);
    }

    current.iter().flatten().filter(|&&p| p == Pixel::On).count()
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
        .map(|row| row.chars().map(Pixel::from_char).collect())
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
    fn enhance(&self, rules: &[Rule]) -> Pixels;
    fn to_two_by_twos(&self) -> Vec<Vec<Pixels>>;
    fn to_three_by_threes(&self) -> Vec<Vec<Pixels>>;
}

impl Enhanceable for Pixels {
    fn enhance(&self, rules: &[Rule]) -> Self {
        if self.len() == 2 || self.len() == 3 {
            let pixels = apply_rules(rules, self.clone());
            pixels.unwrap()
        } else if self.len() % 2 == 0 {
            let twos = self.to_two_by_twos();
            let twos = map_apply_rules(rules, twos);
            let merged: Pixels = merge_agnostic(twos);
            merged
        } else {
            let threes = self.to_three_by_threes();
            let threes = map_apply_rules(rules, threes);
            let merged: Pixels = merge_agnostic(threes);
            merged
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
                let zipper = row1.iter().cloned().zip(row2.iter().cloned().zip(
                    row3.iter().cloned(),
                ));

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

    add_all_rotations(matches, &flipped)
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

fn apply_rules(rules: &[Rule], pixels: Pixels) -> Option<Pixels> {
    rules.iter().find(|rule| rule.matches(&pixels)).map(
        |rule| {
            rule.after.clone()
        },
    )
}

fn map_apply_rules(rules: &[Rule], twos_or_threes: Vec<Vec<Pixels>>) -> Vec<Vec<Pixels>> {
    let mut enhanced: Vec<Vec<Pixels>> = Vec::new();
    for row in twos_or_threes {
        let enhanced_row: Vec<Pixels> = row.into_iter()
            .map(|pixels| apply_rules(rules, pixels).unwrap())
            .collect();
        enhanced.push(enhanced_row);
    }
    enhanced
}

fn merge_two_by_twos(two_by_twos: Vec<Vec<Pixels>>) -> Pixels {
    let mut merged: Pixels = Vec::new();
    for twos_row in &two_by_twos {
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
    for threes_row in &three_by_threes {
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

fn merge_agnostic(stuff: Vec<Vec<Pixels>>) -> Pixels {
    if stuff[0][0].len() % 2 == 0 {
        merge_two_by_twos(stuff)
    } else {
        merge_three_by_threes(stuff)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn enhance_2_by_2() {
        let rules = parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
        let example = parse_pixels(START_STRING);
        let expected = parse_pixels("#..#/..../..../#..#");
        let enhanced = example.enhance(&rules);
        let mut passed = true;
        for (r, row) in expected.iter().enumerate() {
            for (c, p) in row.iter().enumerate() {
                if *p != enhanced[r][c] {
                    passed = false;
                }
            }
        }
        if passed == false {
            panic!("Expected:\n{:#?}\nEnhanced:\n{:#?}", expected, enhanced);
        }
    }

    #[test]
    fn enhance_4_by_4() {
        let rules = parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
        let expected = parse_pixels("##.##./#..#../....../##.##./#..#../......");
        let example = parse_pixels("#..#/..../..../#..#");
        let enhanced = example.enhance(&rules);
        let mut passed = true;
        for (r, row) in expected.iter().enumerate() {
            for (c, p) in row.iter().enumerate() {
                if *p != enhanced[r][c] {
                    passed = false;
                }
            }
        }
        if passed == false {
            panic!("Expected:\n{:#?}\nEnhanced:\n{:#?}", expected, enhanced);
        }
    }

    #[test]
    fn to_three_by_threes_test() {
        let example = parse_pixels("#...../.#..../..#.../...#../....#./.....#");
        let split = example.to_three_by_threes();
        let result = merge_three_by_threes(split);
        assert_eq!(example, result);
    }

    #[test]
    fn solve_test() {
        let rules = parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
        let example = parse_pixels(START_STRING);
        assert_eq!(12, solve(example, &rules, 2));
    }

}
