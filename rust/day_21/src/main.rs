use std::fs;
use std::collections::HashSet;

fn main() {
    // let start_pattern = parse_pixels(".#./..#/###");
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

        for row in 0..size {
            for col in 0..size {
                rotated[row][col] = self[size - col - 1][row];
            }
        }

        rotated
    }

    fn rotate_left(&self) -> Pixels {
        let size = self.len();
        let mut rotated = vec![vec![Pixel::Off; size]; size];
        for _row in 0..size {
            let col = Vec::with_capacity(size);
            rotated.push(col);
        }

        for row in 0..size {
            for col in 0..size {
                rotated[row][col] = self[row][size - col - 1];
            }
        }

        rotated
    }

    fn rotate_180(&self) -> Pixels {
        self.iter().cloned().rev().collect()
    }
}

fn build_rule_match_set(pixels: &Pixels) -> HashSet<Pixels> {
    let mut matches: HashSet<Pixels> = HashSet::new();

    // let pixelses = [
    //     pixels.clone(),

    //     pixels.rotate_right(),
    //     pixels.rotate_right().rotate_right(),
    //     pixels.rotate_right().rotate_right().rotate_right(),

    //     // pixels.rotate_right(),
    //     // pixels.rotate_left(),
    //     // pixels.rotate_180(),

    // ];

    // for p in pixelses.into_iter() {
    //     matches.insert(*p);
    // }

    // this can't be what the borrow checker wanted
    matches.insert(pixels.clone());
    matches.insert(pixels.rotate_right());
    matches.insert(pixels.rotate_left());
    matches.insert(pixels.rotate_180());

    // let rotated = pixels.rotate();
    // matches.insert(rotated);
    // let rotated = rotated.rotate();
    // matches.insert(rotated);
    // let rotated = rotated.rotate();
    // matches.insert(rotated);

    // matches.insert(pixels);

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
}
