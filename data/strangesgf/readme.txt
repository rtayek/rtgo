I used these files to work on the parser for MyGoGrinder.
They are defect or just uncommon code: so e.g. (;) and very long comments.
Use it for your tests of programs and parsers.

They will or will not give errors, exceptions or messages.
They will or will not display correctly.
Some legal files will be criticized by CGoban (this is THE control instance). 
Many completely "illegal" files are accepted by MultiGo.
Sort order may be different, depending on OS and file system (a directory listing may give the file names in order abc or depending on write order in the file table).

I worked around some mistakes, so e.g. 
 * wrong files made by a version of the old program Hibiscus
 * files with leading email header or similar
 * files with WhiteSpace, where they are not to be expected (relics of sending files by email or copying the code from a web page, e.g. a forum page)
 * too long identifiers (e.g.MULTIGOGM[0])

What I still don't handle (dec.2014):
 * pass moves (B[tt] or W[])
 * setups, where stones are removed or replaced by those of the opposite color
 * UTF-8 and other non-ASCII encodings

If you have a file with correct or defect code, which is not accepted or doesn't get the right error message by MyGoGrinder, please send it to me:
rued(point)kle(at)gmx(point)de
Thankyou! Ruediger