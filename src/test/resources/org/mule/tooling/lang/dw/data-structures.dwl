%dw 2.0
ns test http://acme.com/test
---
{
    test#a @(a: "a"): "a",
    b @(b: "b"): [
        0, 1,
        {
             "2" @(c: 2): {
                i: "i",
                ii: "ii",
                iii @(d: "e", f: "g"): [ 0, 1, 2],
                iv: { "0": 0, "1": 1 }
             }
        },
        3
    ],
    test @( attribute : "a") : 123
}