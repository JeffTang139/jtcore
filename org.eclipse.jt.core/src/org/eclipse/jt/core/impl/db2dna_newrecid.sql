create function dna_newrecid()
returns char(16) for bit data
specific dna_newrecid
return cast(hex(generate_unique()) concat substr(hex(floor(rand() * 16777215)),10,6) as char(16) for bit data)