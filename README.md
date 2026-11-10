# Hero Duel

A turn-based hero battle game written in **Java** with a **Swing** GUI.
_(Indonesian title: **Duel Pahlawan**.)_

Pick a hero, then trade blows with a monster until one side runs out of HP. The project is built around clean object-oriented design: encapsulation, inheritance, polymorphism, abstraction, and exception handling.

## Gameplay

| Character | Role | HP | Power | Attack | Special |
|---|---|---|---|---|---|
| Knight (`Ksatria`) | Hero | 120 | 15 | Fixed damage (15) | n/a |
| Mage (`Penyihir`) | Hero | 70 | 12 | Double damage (24) | Heals itself for 25 HP (never above max HP) |
| Monster | Enemy (AI) | 130 | 16 | Random damage (8 to 16) | n/a |

Each turn the player attacks (or heals, as the Mage); the monster then strikes back automatically. Every event is written to a battle log, and the fight ends when either side reaches 0 HP.

## Architecture

```
Karakter (abstract)  <|--  Ksatria
        ^            <|--  Penyihir ..|> BisaMenyembuhkan (interface)
        |            <|--  Monster
Pertarungan  o--  2 x Karakter   (aggregation: hero + enemy)
```

- **Encapsulation:** `Karakter` keeps `hp` private; it only changes through validated `terimaDamage()` / `pulihkanHp()`.
- **Abstraction:** `Karakter` is abstract with an abstract `serang()`; `BisaMenyembuhkan` models the optional healing ability.
- **Inheritance and overriding:** each subclass defines its own attack.
- **Polymorphism:** `Pertarungan` works with `Karakter` references; the right `serang()` runs at runtime, so new character types need no changes to the battle logic.
- **Exception handling:** a checked `KarakterMatiException` for actions on a defeated character.
- **Separation of concerns:** `model` (game logic, no Swing imports) vs `ui` (Swing).

> Class and method names are in Indonesian (`Karakter` = Character, `serang` = attack, `sembuhkan` = heal, `Pertarungan` = Battle).

## Project structure

```
src/duelpahlawan/
  model/    game logic (Karakter, Ksatria, Penyihir, Monster, Pertarungan, ...)
  ui/       Swing user interface (in progress)
uji/        console test runner for the model (UjiCoba.java)
docs/
  diagram/  PlantUML sources: use case, class, sequence, object diagrams
  erd-duel-pahlawan.drawio   conceptual ER diagram (open in draw.io)
```

## Getting started (JDK 17+)

```bash
# compile the model and the test runner
javac -d out $(find src -name "*.java") uji/UjiCoba.java
# run the model tests
java -cp out UjiCoba
```

Windows PowerShell:
`javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName uji/UjiCoba.java`

## Roadmap

- [x] Game model + tests
- [x] UML diagrams and ER diagram
- [ ] Swing GUI (`ui/JendelaUtama`, `Main`)
- [ ] Screenshots

## Contributing

1. Do not commit directly to `main`; create a branch per task (`feature/short-name`).
2. Keep commits small with clear messages, then open a Pull Request and ask for one review.
3. Keep class, attribute, and method names aligned with the diagrams in `docs/diagram`; update the diagrams if you rename anything.
4. Build output and IDE files are ignored via `.gitignore`.

## Contributors

[Devin Rafa Haryanto](https://github.com/DevinRafa), Najmi, Willy, Fardizza, Rafi
