## How to run

### REPL (Read-Eval-Print Loop)

```console
$ java lang.Main
```

### Code from a file

```console
$ java lang.Main <filename>
```

## Features

### Comments

```
(this will be ignored) 1 print
( Output:
1.0
)

(even ( nested ) parens will ( be (ignored))) 1 print
( Output:
1.0
)
```

### Basic arithmetics

```
1 2 + print
( Output:
3.0
)
```

### If conditions

```
if 1 2 < do
    1 print
end
( Output:
1.0
)

if 1 2 > do
    1 print
end
( Output:
)
```

### While loops

```
5
while 1dup 0 > do
    1dup print
    1 -
end
1drop
( Output:
5.0
4.0
3.0
2.0
1.0
)
```

## Functions

### Notation
`a, b -> c, d`
- The stack is expected to have two values, `a` and `b`, on top where `b` is on top of `a`.
- After the function is finished, the stack will have two values, `c` and `d`, on top where `d` is on top of `c`.

### Standard functionality

#### Stack manipulation
- `<n>dup`: Duplicates the top `n` values from the stack (e.g. `2dup` duplicates the top 2 values) (`a1, a2, ..., an -> a1, a2, ..., an, a1, a2, ..., an`)
- `<n>drop`: Removes the top `n` values from the stack and discards them (e.g. `2drop` discards the top 2 values) (`a1, a2, ..., an ->`)
- `reverse`: Reverses the whole stack (`a, b, ..., x -> x, ..., b, a`)
- `<n>reverse`: Reverses the top `n` values from the stack (e.g. `3reverse` reverses the top 3 values) (a1, a2, ..., an -> an, ..., a2, a1)
- `<m>swap<n>`: Swaps the top `n` values and the next `m` elements (e.g. `2swap2` performs `a, b, c, d -> c, d, a, b`) (`a1, a2, ..., am, b1, b2, ..., bn -> b1, b2, ..., bn, a1, a2, ..., am`)
- `<m>over<n>`: Copies the next `m` elements over the top `n` elements (e.g. `2over2` performs `a, b, c, d -> a, b, c, d, a, b`) (`a1, a2, ..., am, b1, b2, ..., bn -> a1, a2, ..., am, b1, b2, ..., bn, a1, a2, ..., am`)

#### Arithmetics
- `+`: Sums the top 2 values from the stack and pushes the result back (`a, b -> a + b`)
- `-`: Subtracts the top 2 values from the stack and pushes the result back (`a, b -> a - b`)
- `*`: Multiplies the top 2 values from the stack and pushes the result back (`a, b -> a * b`)
- `/`: Divides the top 2 values from the stack and pushes the result back (`a, b -> a / b`)
- `%`: Computes the top value modulo the second from the top value and pushes the result back (`a, b -> a % b`)
- `divmod`: A combination of `/` and `%` (`a, b -> a / b, a % b`)
- `pow`: Computes the value of the second from the top value to the power of the top value and pushes the result back (`a, b -> pow(a, b)`)
- `sqrt`: Computes the square root of the number on top and pushes the result back (`a -> sqrt(a)`)

#### Trigonometry
- `sin`: Calculates the sine of the top value of the stack and pushes the result back (`a -> sin(a)`)
- `cos`: Calculates the cosine of the top value of the stack and pushes the result back (`a -> cos(a)`)

#### Comparisons
- `<`: Compares the top two values and pushes `1` if the top value is bigger, otherwise pushes `0` (`a, b -> a < b`)
- `<`: Compares the top two values and pushes `1` if the top value is smaller, otherwise pushes `0` (`a, b -> a > b`)

#### Debugging
- `print`: Prints the top value from the stack and discards it (`a --`)

## To Do list

### Functions/procedures

```
fn add + end
1 2 add
```

### Other data types

```
"Hello, World" print
```

### Type checks
```
1 + (should raise an error before the code is run)
```
- Figure syntax for function definitions
- Figure how to make sure `if`s and `while`s don't alter the stack
