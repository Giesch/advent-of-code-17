#![feature(drain_filter)]

use std::fs::read_to_string;
use std::collections::HashMap;

fn main() {
    let mut particles: Vec<Particle> = read_input("problem.txt");

    for _ in 0..1_000 {
        update_particles(&mut particles);
        remove_colliding(&mut particles);
    }

    let closest = closest_to_center(&particles);
    println!("{:#?}", closest);

    println!("particles left: {}", particles.len());
}

fn read_input(file_name: &str) -> Vec<Particle> {
    read_to_string(file_name)
        .unwrap()
        .lines()
        .enumerate()
        .map(|(index, line)| Particle::from_string(line, index))
        .collect()
}

fn vector_from_string(s: &str) -> [i64; 3] {
    let unbracketed = s.split(|c| c == '<' || c == '>').collect::<Vec<&str>>()[1];
    let numbers = unbracketed.split(',').collect::<Vec<&str>>();

    [
        numbers[0].parse().unwrap(),
        numbers[1].parse().unwrap(),
        numbers[2].parse().unwrap(),
    ]
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
struct Particle {
    position: [i64; 3],
    velocity: [i64; 3],
    acceleration: [i64; 3],
    index: usize,
}

impl Particle {
    fn from_string(string: &str, index: usize) -> Particle {
        let tokens = string.split_whitespace().collect::<Vec<&str>>();

        Particle {
            position: vector_from_string(tokens[0]),
            velocity: vector_from_string(tokens[1]),
            acceleration: vector_from_string(tokens[2]),
            index,
        }
    }

    fn distance_from_center(&self) -> i64 {
        self.position.iter().map(|scalar| scalar.abs()).sum::<i64>()
    }
}

fn remove_colliding(particles: &mut Vec<Particle>) {
    let position_counts = particles.iter().map(|particle| particle.position).fold(
        HashMap::new(),
        |mut map, position| {
            *map.entry(position).or_insert(0) += 1;
            map
        },
    );

    particles.drain_filter(|particle| {
        *position_counts.get(&particle.position).unwrap() > 1
    });
}

fn update_particles(particles: &mut Vec<Particle>) {
    for mut p in particles {
        p.velocity[0] += p.acceleration[0];
        p.velocity[1] += p.acceleration[1];
        p.velocity[2] += p.acceleration[2];
        p.position[0] += p.velocity[0];
        p.position[1] += p.velocity[1];
        p.position[2] += p.velocity[2];
    }
}

fn closest_to_center(particles: &Vec<Particle>) -> &Particle {
    particles
        .iter()
        .min_by_key(|p| p.distance_from_center())
        .unwrap()
}
