use std::fs;
use std::collections::HashSet;

fn main() {
    let start_pattern = parse_pixels(".#./..#/###");
    // println!("{:#?}", start_pattern);

    let rules = read_input("input.txt");

    // let matrix = &rules[50].before;
    // println!("{:#?}", matrix);
    // let rotated = rotate(matrix);
    // println!("{:#?}", rotated);
}

fn read_input(filename: &str) -> Vec<Rule> {
    fs::read_to_string(filename)
        .unwrap()
        .lines()
        .map(|line| Rule::from_string(line))
        .collect()
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
        for _row in 0..size {
            let col = Vec::with_capacity(size);
            rotated.push(col);
        }

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
    fn enhance(&self, rules: impl Iterator<Item = Rule>) -> &Self;
    fn to_two_by_twos(&self) -> Vec<Vec<Pixels>>;
}

impl Enhanceable for Pixels {
    fn enhance(&self, rules: impl Iterator<Item = Rule>) -> &Self {
        if self.len() % 2 == 0 {
            // even, break into 2x2 and replace each with rules
        } else {
            // odd & multiple of 3, break into 3x3 and replace each with rules
        }

        self
    }

    fn to_two_by_twos(&self) -> Vec<Vec<Pixels>> {
        self.chunks(2).map(|row_pair| {
            let two_rows_of_pairs = row_pair
                .into_iter()
                .map(|row: &Vec<Pixel>| row.chunks(2).collect::<Vec<_>>())
                .collect::<Vec<_>>();

            let row1 = &two_rows_of_pairs[0];
            let row2 = &two_rows_of_pairs[1];

            // much clone wow
            let zipper = row1.iter().cloned().zip(row2.iter().cloned());
            // this rotates the 2x2s, which should be fine
            zipper.map(|(pair1, pair2)| vec![pair1.into(), pair2.into()]).collect::<Vec<_>>()
        }).collect()
    }
}

fn build_rule_match_set(pixels: &Pixels) -> HashSet<Pixels> {
    let matches: HashSet<Pixels> = HashSet::new();

    let add_all_rotations = |mut matches: HashSet<Pixels>, ps: &Pixels| {
        matches.insert(ps.clone());
        matches.insert(ps.rotate_right());
        matches.insert(ps.rotate_left());
        matches.insert(ps.rotate_180());
        matches
    };

    let matches = add_all_rotations(matches, pixels);

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

        Rule {
            before,
            matches,
            after: parse_pixels(tokens[1]),
        }
    }

    fn matches(&self, pixels: &Pixels) -> bool {
        self.matches.contains(pixels)
    }
}
