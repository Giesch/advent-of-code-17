use std::fs::read_to_string;

fn main() {
    let string = read_to_string("input.txt").unwrap();
    let diagram = read_input(&string);

    let mut state = State::inital_state(diagram);

    while !state.done() {
        state.advance();
    }

    // println!("Final state: {:#?}", state);
    println!(
        "Characters: {}",
        state.characters_seen.iter().collect::<String>()
    );
    println!("Steps: {}", state.steps);
}

type Diagram = Vec<Vec<char>>;
type Position = (usize, usize);

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
enum Direction {
    Up,
    Down,
    Left,
    Right,
}

impl Direction {
    fn opposite(&self) -> Direction {
        use Direction::*;

        match self {
            Up => Down,
            Down => Up,
            Left => Right,
            Right => Left,
        }
    }
}

#[derive(Debug)]
struct State {
    steps: u32,
    direction: Direction,
    position: Position,
    diagram: Diagram,
    characters_seen: Vec<char>,
}

pub fn read_input(input: &str) -> Diagram {
    input
        .lines()
        .map(|line| line.chars().collect())
        .collect::<Vec<Vec<char>>>()
}

impl State {
    fn inital_state(diagram: Diagram) -> Self {
        let start = (0, diagram[0].iter().position(|&ch| ch == '|').unwrap());
        Self {
            steps: 0,
            diagram: diagram,
            direction: Direction::Down,
            characters_seen: vec![],
            position: start,
        }
    }

    fn next_position(&self, direction: Direction) -> Position {
        use Direction::*;

        match direction {
            Right => (self.position.0, self.position.1 + 1),
            Left => (self.position.0, self.position.1 - 1),
            Up => (self.position.0 - 1, self.position.1),
            Down => (self.position.0 + 1, self.position.1),
        }
    }

    fn lookup(&self, position: Position) -> Option<&char> {
        // println!("Performing lookup: {:#?}", position);
        self.diagram.get(position.0)?.get(position.1)
    }

    fn current_char(&self) -> char {
        *self.lookup(self.position).unwrap()
    }

    fn look_ahead(&self, direction: Direction) -> Option<&char> {
        self.lookup(self.next_position(direction))
    }

    fn choose_direction(&self) -> &Direction {
        use Direction::*;

        [Up, Down, Left, Right]
            .iter()
            .filter(|&dir| if let Some(&ch) = self.look_ahead(*dir) {
                ch != ' ' && dir.opposite() != self.direction
            } else {
                false
            })
            .collect::<Vec<&Direction>>()
            [0]
    }

    fn advance(&mut self) {
        // println!("advancing: {:#?}", self);
        let ch = self.current_char();

        match ch {
            '+' => {
                self.direction = *self.choose_direction();
            }
            '|' | '-' => (),
            ' ' => panic!("advanced on blank square"),
            _ => {
                self.characters_seen.push(ch);
            }
        }

        self.position = self.next_position(self.direction);
        self.steps += 1;
    }

    fn done(&self) -> bool {
        self.current_char() == ' '
    }
}
