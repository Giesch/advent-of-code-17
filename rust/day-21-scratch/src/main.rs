#![feature(iterator_flatten)]

use std::collections::{HashMap, HashSet};
use std::fs;
use std::hash::Hash;

const START_STRING: &str = ".#./..#/###";

type Pixels = Vec<Vec<bool>>;
type TwoByTwo = ((bool, bool), (bool, bool));
type ThreeByThree = ((bool, bool, bool), (bool, bool, bool), (bool, bool, bool));

fn main() {
    let start_pattern = parse_pixels(START_STRING);
    let rules = read_input("input.txt");
    let solution = solve(start_pattern, rules, 5);
    println!("{}", solution);
}

fn apply_rules(rules: &HashMap<Pixels, Pixels>, pixels: Pixels) -> Pixels {
    if let Some(rule) = rules.get(&pixels) {
        rule.clone()
    } else {
        panic!(
            "Missing rule for pixels:\n{:#?}\nWith rules:\n{:#?}",
            pixels,
            rules
        );
    }
}

fn solve(start_pattern: Pixels, rules: HashMap<Pixels, Pixels>, iterations: usize) -> usize {
    let mut current = start_pattern;
    for _ in 0..iterations {
        current = apply_rules(&rules, current);
    }
    current.iter().flatten().filter(|boo| **boo).count()
}

fn read_input(filename: &str) -> HashMap<Pixels, Pixels> {
    let input = fs::read_to_string(filename).unwrap();
    parse_rules(&input)
}

fn parse_rules(input: &str) -> HashMap<Pixels, Pixels> {
    let mut rules: HashMap<Pixels, Pixels> = HashMap::new();
    for (before, after) in input.lines().map(parse_rule) {
        let keys: HashSet<Pixels> = before.all_transformations();
        for key in keys.into_iter() {
            rules.insert(key, after.clone());
        }
    }
    rules
}

fn parse_rule(line: &str) -> (Pixels, Pixels) {
    let mut iter = line.split(" => ");
    let before = iter.next().expect("invalid rule");
    let after = iter.next().expect("invalid rule");
    let before = parse_pixels(before);
    let after = parse_pixels(after);
    (before, after)
}

fn parse_pixels(s: &str) -> Pixels {
    s.split('/')
        .map(|row| row.chars().map(|c| c == '#').collect())
        .collect()
}

trait TwoDimensional
where
    Self: Sized + Eq + Hash + Clone,
{
    fn rotate(&self) -> Self;
    fn all_rotations(&self) -> HashSet<Self> {
        let mut rotations: HashSet<Self> = HashSet::new();
        rotations.insert(self.rotate());
        rotations.insert(self.rotate().rotate());
        rotations.insert(self.rotate().rotate().rotate());
        rotations
    }
    fn flip_horizontal(&self) -> Self;
    fn flip_vertical(&self) -> Self;
    fn all_transformations(&self) -> HashSet<Self> {
        let add_all_flips = |mut transforms: HashSet<Self>| {
            let mut flips: HashSet<Self> = HashSet::new();
            for t in transforms.iter() {
                flips.insert(t.flip_horizontal());
                flips.insert(t.flip_vertical());
            }
            transforms.extend(flips);
            transforms
        };

        let mut transformations: HashSet<Self> = HashSet::new();
        transformations.insert(self.clone());
        transformations.extend(self.all_rotations());
        let transformations = add_all_flips(transformations);
        transformations
    }
}

impl TwoDimensional for Pixels {
    fn flip_horizontal(&self) -> Pixels {
        self.iter()
            .map(|row| row.iter().cloned().rev().collect())
            .collect()
    }

    fn flip_vertical(&self) -> Pixels {
        self.iter().cloned().rev().collect()
    }

    fn rotate(&self) -> Pixels {
        let size = self.len();
        let mut rotated = vec![vec![false; size]; size];
        for row in 0..size {
            for col in 0..size {
                rotated[row][col] = self[size - col - 1][row];
            }
        }

        rotated
    }
}

impl TwoDimensional for TwoByTwo {
    fn flip_horizontal(&self) -> TwoByTwo {
        let ((a, b), (c, d)) = self;
        ((*b, *a), (*d, *c))
    }

    fn flip_vertical(&self) -> TwoByTwo {
        let ((a, b), (c, d)) = self;
        ((*c, *d), (*a, *b))
    }

    fn rotate(&self) -> TwoByTwo {
        let ((a, b), (c, d)) = self;
        ((*c, *a), (*d, *b))
    }
}

impl TwoDimensional for ThreeByThree {
    fn flip_horizontal(&self) -> ThreeByThree {
        let ((a, b, c), (d, e, f), (g, h, i)) = self;
        ((*c, *b, *a), (*f, *e, *d), (*i, *h, *g))
    }

    fn flip_vertical(&self) -> ThreeByThree {
        let ((a, b, c), (d, e, f), (g, h, i)) = self;
        ((*g, *h, *i), (*d, *e, *f), (*a, *b, *c))
    }

    fn rotate(&self) -> ThreeByThree {
        let ((a, b, c), (d, e, f), (g, h, i)) = self;
        ((*g, *d, *a), (*h, *e, *b), (*i, *f, *c))
    }
}

trait Enhanceable {
    fn to_two_by_twos(&self) -> Vec<Vec<TwoByTwo>>;
    // fn to_three_by_threes(&self) -> Vec<Vec<ThreeByThree>>;
    // fn enhance(&self, rules: HashMap<Pixels, Pixels>) -> Pixels;
}

impl Enhanceable for Pixels {
    fn to_two_by_twos(&self) -> Vec<Vec<TwoByTwo>> {
        self.chunks(2)
            .map(|two_rows| {
                let two_rows_of_pairs = two_rows
                    .into_iter()
                    .map(|row: &Vec<bool>| row.chunks(2).collect::<Vec<_>>())
                    .collect::<Vec<_>>();
                let row1 = &two_rows_of_pairs[0];
                let row2 = &two_rows_of_pairs[1];
                let zipper = row1.iter().cloned().zip(row2.iter().cloned());
                zipper
                    .map(|(pair1, pair2)| vec![pair1.into(), pair2.into()])
                    .collect::<Vec<_>>()
            })
            .collect()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_parses() {
        parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
    }

    #[test]
    fn flip_vertical_test() {
        let pixels = vec![
            vec![true, true, true],
            vec![false, false, false],
            vec![false, false, false],
        ];
        let result = pixels.flip_vertical();
        let expected = vec![
            vec![false, false, false],
            vec![false, false, false],
            vec![true, true, true],
        ];
        assert_eq!(expected, result);
    }

    #[test]
    fn flip_horizontal_test() {
        let pixels = vec![
            vec![true, false, false],
            vec![true, false, false],
            vec![true, false, false],
        ];
        let result = pixels.flip_horizontal();
        let expected = vec![
            vec![false, false, true],
            vec![false, false, true],
            vec![false, false, true],
        ];
        assert_eq!(expected, result);
    }

    #[test]
    fn rotation_test() {
        let pixels = vec![
            vec![true, false, false],
            vec![true, false, false],
            vec![true, false, false],
        ];
        let expected = vec![
            vec![true, true, true],
            vec![false, false, false],
            vec![false, false, false],
        ];
        let result = pixels.rotate();
        assert_eq!(expected, result);
    }

    #[test]
    fn all_rotations_test() {
        let pixels = vec![
            vec![true, false, false],
            vec![true, false, false],
            vec![true, false, false],
        ];
        let result: HashSet<Pixels> = pixels.all_rotations();

        let expected_one = vec![
            vec![true, true, true],
            vec![false, false, false],
            vec![false, false, false],
        ];
        assert!(result.contains(&expected_one));
        let expected_two = vec![
            vec![false, false, true],
            vec![false, false, true],
            vec![false, false, true],
        ];
        assert!(result.contains(&expected_two));
        let expected_three = vec![
            vec![false, false, false],
            vec![false, false, false],
            vec![true, true, true],
        ];
        assert!(result.contains(&expected_three));
    }

    #[test]
    fn all_rotations_and_flips_test() {
        let pixels = vec![
            vec![true, false, false],
            vec![true, false, false],
            vec![false, false, false],
        ];
        let result = pixels.all_transformations();

        assert!(result.contains(&pixels));
        let expected_one = vec![
            vec![false, true, true],
            vec![false, false, false],
            vec![false, false, false],
        ];
        assert!(result.contains(&expected_one));
        let expected_two = vec![
            vec![false, false, false],
            vec![false, false, true],
            vec![false, false, true],
        ];
        assert!(result.contains(&expected_two));
        let expected_three = vec![
            vec![false, false, false],
            vec![false, false, false],
            vec![true, true, false],
        ];
        assert!(result.contains(&expected_three));

        let expected_four = vec![
            vec![false, false, false],
            vec![true, false, false],
            vec![true, false, false],
        ];
        assert!(result.contains(&expected_four));
        let expected_five = vec![
            vec![false, false, true],
            vec![false, false, true],
            vec![false, false, false],
        ];
        assert!(result.contains(&expected_five));
    }

    #[test]
    fn rotation_flips_test() {
        let pixels = vec![
            vec![false, false, false, false, false, false],
            vec![true, false, false, false, false, false],
            vec![true, true, false, false, false, false],
            vec![false, false, false, false, false, false],
            vec![false, false, false, false, false, false],
            vec![false, false, false, false, false, false],
        ];

        let expected = vec![
            vec![false, false, false, false, false, false],
            vec![false, false, false, false, false, false],
            vec![false, false, false, false, false, false],
            vec![false, false, false, false, false, false],
            vec![false, false, false, true, false, false],
            vec![false, false, false, true, true, false],
        ];
        let result = pixels.all_transformations();
        assert!(result.contains(&expected));
    }

    // #[test]
    // fn first_iteration() {
    //     let rules = parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
    //     let example = parse_pixels(START_STRING);
    //     let expected = vec![
    //         vec![true, false, false, true],
    //         vec![false, false, false, false],
    //         vec![false, false, false, false],
    //         vec![true, false, false, true],
    //     ];
    //     let result = apply_rules(&rules, example);
    //     let mut passed = true;
    //     for (r, row) in expected.iter().enumerate() {
    //         for (c, p) in row.iter().enumerate() {
    //             if *p != result[r][c] {
    //                 passed = false;
    //             }
    //         }
    //     }
    //     if passed == false {
    //         panic!("Expected:\n{:#?}\nEnhanced:\n{:#?}", expected, result);
    //     }
    // }

    // #[test]
    // fn second_iteration() {
    //     let rules = parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
    //     let example = parse_pixels(START_STRING);
    //     let expected = vec![
    //         vec![true, false, false, true],
    //         vec![false, false, false, false],
    //         vec![false, false, false, false],
    //         vec![true, false, false, true],
    //     ];
    //     let result = apply_rules(&rules, example);
    //     let result = apply_rules(&rules, result);
    //     let mut passed = true;
    //     for (r, row) in expected.iter().enumerate() {
    //         for (c, p) in row.iter().enumerate() {
    //             if *p != result[r][c] {
    //                 passed = false;
    //             }
    //         }
    //     }
    //     if passed == false {
    //         panic!("Expected:\n{:#?}\nEnhanced:\n{:#?}", expected, result);
    //     }
    // }

    // #[test]
    // fn solve_test() {
    //     let rules = parse_rules("../.# => ##./#../...\n.#./..#/### => #..#/..../..../#..#");
    //     let example = parse_pixels(START_STRING);
    //     let solution = solve(example, rules, 2);
    //     assert_eq!(12, solution);
    // }

}
