package pt.ipp.estg.myapplication.enumerations

fun getIdByName(districtName: String): Int {
    var id = 1
    when (districtName) {
        "Aveiro" -> id = 1
        "Beja" -> id = 2
        "Braga" -> id = 3
        "Bragança" -> id = 4
        "Castelo Branco" -> id = 5
        "Coimbra" -> id = 6
        "Évora" -> id = 7
        "Faro" -> id = 8
        "Guarda" -> id = 9
        "Leiria" -> id = 10
        "Lisboa" -> id = 11
        "Portalegre" -> id = 12
        "Porto" -> id = 13
        "Santarém" -> id = 14
        "Setúbal" -> id = 15
        "Viana do Castelo" -> id = 16
        "Vila Real" -> id = 17
        "Viseu" -> id = 18
    }
    return id
}

enum class DistrictNames(val id: Int, val nameDistrict: String) {
    AVEIRO(1, "Aveiro"),
    BEJA(2, "Beja"),
    BRAGA(3, "Braga"),
    BRAGANCA(4, "Bragança"),
    CASTELOBRANCO(5, "Castelo Branco"),
    COIMBRA(6, "Coimbra"),
    EVORA(7, "Évora"),
    FARO(8, "Faro"),
    GUARDA(9, "Guarda"),
    LEIRIA(10, "Leiria"),
    LISBOA(11, "Lisboa"),
    PORTALEGRE(12, "Portalegre"),
    PORTO(13, "Porto"),
    SANTAREM(14, "Santarém"),
    SETUBAL(15, "Setúbal"),
    VIANADOCASTELO(16, "Viana do Castelo"),
    VILAREAL(17, "Vila Real"),
    VISEU(18, "Viseu"),
}