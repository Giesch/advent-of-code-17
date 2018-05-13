use std::fs;

fn main() {
    // let start_pattern = parse_pixels(".#./..#/###");
    // println!("{:#?}", start_pattern);
    let rules = read_input("input.txt");
    println!("{:#?}", rules);
}

fn read_input(filename: &str) -> Vec<Rule> {
    fs::read_to_string(filename)
        .unwrap()
        .lines()
        .map(|line| Rule::from_string(line))
        .collect()
}

type Pixels = Vec<Vec<Pixel>>;

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
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
}

// fn rotate(vs: Vec<Vec<Pixel>>) -> Vec<Vec<Pixel>> {
//     for row in vs {
//     }
// }

impl Rule {
    fn from_string(line: &str) -> Rule {
        let tokens: Vec<&str> = line.split(" => ").collect();

        Rule {
            before: parse_pixels(tokens[0]),
            after: parse_pixels(tokens[1]),
        }
    }
}
