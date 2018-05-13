use std::fs::read_to_string;

fn main() {
    let particles: Vec<Particle> = read_input("problem.txt");

    // println!("{:#?}", particles);
}

fn read_input(file_name: &str) -> Vec<Particle> {
    read_to_string(file_name)
        .unwrap()
        .lines()
        .map(|line| Particle::from_string(line))
        .collect()
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
struct Vector3 {
    x: i32,
    y: i32,
    z: i32,
}

impl Vector3 {
    fn from_string(s: &str) -> Vector3 {
        let unbracketed = s.split(|c| c == '<' || c == '>').collect::<Vec<&str>>()[1];
        let numbers = unbracketed.split(',').collect::<Vec<&str>>();

        Vector3 {
            x: numbers[0].parse().unwrap(),
            y: numbers[1].parse().unwrap(),
            z: numbers[2].parse().unwrap(),
        }
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
struct Particle {
    position: Vector3,
    velocity: Vector3,
    acceleration: Vector3,
}

impl Particle {
    fn from_string(string: &str) -> Particle {
        let string = string.split_whitespace().collect::<Vec<&str>>();
        let position = Vector3::from_string(string[0]);
        let velocity = Vector3::from_string(string[1]);
        let acceleration = Vector3::from_string(string[2]);

        Particle {
            position,
            velocity,
            acceleration,
        }

        // let position = string[0].split(|c| c == '<' || c == '>').collect::<Vec<&str>>()[1];
    }
}

// fn read_input(filename: &str) -> Vec<Particle> {
// }
