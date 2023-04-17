# Dialogue generator

Generates random dialogue based on some simple recursive syntax. 


| Syntax              | Results in                    | Example output |
| ------------------- | ----------------------------- | -------------- |
| Regular text.       | Same thing verbatim           | Regular text.  |
| greeting = hello    | Defines a named expression    |                |
| [big large huge]    | Chooses one at random         | huge           |
| (a potato)          | Group words together          | a potato       |
| {rather}            | Randomly use or discard       | rather         |
| &lt;greeting>       | Insert a named expression     | hello          |
| &lt;Greeting>       | Capitalised named expression  | Hello          |
| &lt;GREETING>       | All-caps named expression     | HELLO          |


## Example usage:
```
greeting = [hello hi yo]
its_nice = [([(it is) it's] nice) (I'm {very} glad)]
see = [see meet (run into)]
take_back = [(or is it) not (I don't think)]

message = <Greeting>[! .] <Its_nice> to <see> you. {<TAKE_BACK>!} 
```
## 10 results for &lt;message>:
```
Yo! It is nice to see you. NOT!
Yo! I'm very glad to run into you. OR IS IT!
Yo! It's nice to see you.
Hi! It's nice to see you.
Hello. I'm very glad to run into you.
Hi! It is nice to meet you.
Yo. I'm glad to meet you. NOT!
Yo! I'm very glad to run into you. I DON'T THINK!
Yo. I'm very glad to see you.
Hello. It's nice to see you.
``` 

## Implementation
The parser is adapted from [Monadic Parser Combinators](https://www.cs.nott.ac.uk/~pszgmh/monparsing.pdf) by Hutton & Meijer. I'm pretty happy how it turned out with an idiomatic Scala OO/FP hybrid design. I'm sure I'll repurpose `Parser.scala` for all kinds of things.

## History
This is a resurrection of some fun C++ game code I wrote in 2003. This code is much better, and smaller. The brackety syntax was chosen because it is very easy to parse; but I think it's quite easy to read as well. 

