package com.example.pokedexkmp.data

object PokemonMock {

    val pokedex = listOf(
        Pokemon(
            id = 1,
            name = "bulbasaur",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
            types = listOf("grass", "poison"),
            height = 7,
            weight = 69,
            stats = listOf(
                PokemonStat("hp", 45),
                PokemonStat("attack", 49),
                PokemonStat("defense", 49),
                PokemonStat("special-attack", 65),
                PokemonStat("special-defense", 65),
                PokemonStat("speed", 45)
            ),
            description = "Bulbasaur carrega uma semente em suas costas desde o nascimento."
        ),
        Pokemon(
            id = 4,
            name = "charmander",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
            types = listOf("fire"),
            height = 6,
            weight = 85,
            stats = listOf(
                PokemonStat("hp", 39),
                PokemonStat("attack", 52),
                PokemonStat("defense", 43),
                PokemonStat("special-attack", 60),
                PokemonStat("special-defense", 50),
                PokemonStat("speed", 65)
            ),
            description = "Charmander possui uma chama na ponta da cauda que indica sua vitalidade."
        ),
        Pokemon(
            id = 7,
            name = "squirtle",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
            types = listOf("water"),
            height = 5,
            weight = 90,
            stats = listOf(
                PokemonStat("hp", 44),
                PokemonStat("attack", 48),
                PokemonStat("defense", 65),
                PokemonStat("special-attack", 50),
                PokemonStat("special-defense", 64),
                PokemonStat("speed", 43)
            ),
            description = "Squirtle se protege com seu casco e lança jatos d’água."
        ),
        Pokemon(
            id = 25,
            name = "pikachu",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
            types = listOf("electric"),
            height = 4,
            weight = 60,
            stats = listOf(
                PokemonStat("hp", 35),
                PokemonStat("attack", 55),
                PokemonStat("defense", 40),
                PokemonStat("special-attack", 50),
                PokemonStat("special-defense", 50),
                PokemonStat("speed", 90)
            ),
            description = "Pikachu armazena eletricidade em suas bochechas."
        ),
        Pokemon(
            id = 39,
            name = "jigglypuff",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/39.png",
            types = listOf("normal", "fairy"),
            height = 5,
            weight = 55,
            stats = listOf(
                PokemonStat("hp", 115),
                PokemonStat("attack", 45),
                PokemonStat("defense", 20),
                PokemonStat("special-attack", 45),
                PokemonStat("special-defense", 25),
                PokemonStat("speed", 20)
            ),
            description = "Jigglypuff encanta adversários com sua canção."
        ),
        Pokemon(
            id = 133,
            name = "eevee",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/133.png",
            types = listOf("normal"),
            height = 3,
            weight = 65,
            stats = listOf(
                PokemonStat("hp", 55),
                PokemonStat("attack", 55),
                PokemonStat("defense", 50),
                PokemonStat("special-attack", 45),
                PokemonStat("special-defense", 65),
                PokemonStat("speed", 55)
            ),
            description = "Eevee possui uma estrutura genética instável e várias evoluções possíveis."
        ),
        Pokemon(
            id = 94,
            name = "gengar",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/94.png",
            types = listOf("ghost", "poison"),
            height = 15,
            weight = 405,
            stats = listOf(
                PokemonStat("hp", 60),
                PokemonStat("attack", 65),
                PokemonStat("defense", 60),
                PokemonStat("special-attack", 130),
                PokemonStat("special-defense", 75),
                PokemonStat("speed", 110)
            ),
            description = "Esconde-se nas sombras e rouba o calor do ambiente."
        ),
        Pokemon(
            id = 143,
            name = "snorlax",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/143.png",
            types = listOf("normal"),
            height = 21,
            weight = 4600,
            stats = listOf(
                PokemonStat("hp", 160),
                PokemonStat("attack", 110),
                PokemonStat("defense", 65),
                PokemonStat("special-attack", 65),
                PokemonStat("special-defense", 110),
                PokemonStat("speed", 30)
            ),
            description = "Passa a maior parte do tempo dormindo ou comendo."
        ),
        Pokemon(
            id = 149,
            name = "dragonite",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/149.png",
            types = listOf("dragon", "flying"),
            height = 22,
            weight = 2100,
            stats = listOf(
                PokemonStat("hp", 91),
                PokemonStat("attack", 134),
                PokemonStat("defense", 95),
                PokemonStat("special-attack", 100),
                PokemonStat("special-defense", 100),
                PokemonStat("speed", 80)
            ),
            description = "Um Pokémon bondoso que salva navios perdidos em tempestades."
        ),
        Pokemon(
            id = 150,
            name = "mewtwo",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png",
            types = listOf("psychic"),
            height = 20,
            weight = 1220,
            stats = listOf(
                PokemonStat("hp", 106),
                PokemonStat("attack", 110),
                PokemonStat("defense", 90),
                PokemonStat("special-attack", 154),
                PokemonStat("special-defense", 90),
                PokemonStat("speed", 130)
            ),
            description = "Criado por manipulação genética, possui um poder psíquico avassalador."
        )
    )

    fun findById(id: Int): Pokemon? = pokedex.firstOrNull { it.id == id }
}