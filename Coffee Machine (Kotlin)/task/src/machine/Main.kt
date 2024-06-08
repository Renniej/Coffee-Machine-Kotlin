package machine

class Ingredient(val name : String, var amount : Int, val unit : String)

class MenuItem(val name : String, val cost : Double,  val ingredients: MutableList<Ingredient>) {
    fun getIngredient(name : String) : Ingredient? {
        return ingredients.find { name == it.name }
    }
}

class Shop(var cashAmount : Double) {

    fun takeCash() : Double {
        val prevAmount = cashAmount
        cashAmount = 0.0;
        return prevAmount;

    }
}

val Menu  = listOf(
    MenuItem("espresso", 4.0, mutableListOf(
        Ingredient("water", 250,"ml"),
        Ingredient("coffee beans", 16,"g"),
        )),

    MenuItem("latte", 7.0, mutableListOf(
        Ingredient("water", 350,"ml"),
        Ingredient("milk", 75,"ml"),
        Ingredient("coffee beans", 20,"g"),
    )),

    MenuItem("cappuccino",6.0, mutableListOf(
        Ingredient("water", 200,"ml"),
        Ingredient("milk", 100,"ml"),
        Ingredient("coffee beans", 12,"g"),
    )),
)






class CoffeeMachine {


    var availableCups = 9
        private set

    private val supply : MutableMap<String,Ingredient> = mutableMapOf();

    fun addCups(i : Int) {
        availableCups+=i;
    }


    private fun getIngredient(name : String)  : Ingredient?{
        return supply[name]
    }


    fun addIngredient(ingredient: Ingredient) {

        val foundIngredient = getIngredient(ingredient.name)

        if (foundIngredient != null)
            foundIngredient.amount += ingredient.amount
        else
            supply[ingredient.name] = ingredient

    }


    private fun getMinCups( recipe: MenuItem) : Int {

        val numCups  : MutableList<Double> = mutableListOf();

        recipe.ingredients.forEach { recIng ->
                val foundIngredient = supply[recIng.name]
               numCups += if ( foundIngredient != null)
                    foundIngredient.amount.toDouble() / recIng.amount.toDouble()
                else
                    Double.MAX_VALUE
        }

        val minCups : Double = numCups.minOrNull() ?: 0.0


        return  minCups.toInt();

    }
    


    fun makeCup(recipe : MenuItem) : Boolean{
        
        val canMake = getMinCups((recipe)) > 0
        
        if (canMake) {
            for (ingredient in recipe.ingredients) {
                getIngredient(ingredient.name)?.let { supplyIngredient ->
                    supplyIngredient.amount -= ingredient.amount
                }
            }

            availableCups--;
        }

        
        return  canMake
    }

    override fun toString() : String {

        val strBuilder = StringBuilder();
        strBuilder.append("The coffee machine has:\n")

        supply.forEach{
            val ingredient = it.value
            strBuilder.append("${ingredient.amount} ${ingredient.unit} of ${ingredient.name}\n")
        }

        strBuilder.append("$availableCups disposable cups")

        return strBuilder.toString()
    }

}

fun buy(coffeeMachine: CoffeeMachine, shop: Shop) {
    println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino: ")

    val choice = when(readln().toInt()) {
        1 -> "espresso"
        2 -> "latte"
        3-> "cappuccino"
        else -> throw IllegalArgumentException("Invalid menu item choice")
    }

    val menuItem = Menu.find { it.name == choice }
    if (menuItem == null)  throw Exception("menu item $choice does not exist in Menu variable")


    with(coffeeMachine) {
        if (makeCup(menuItem)) {
            shop.cashAmount += menuItem.cost
        }
    }



}


fun fill(coffeeMachine: CoffeeMachine){
    val options = mapOf("water" to "ml",  "milk" to "ml", "coffee beans" to "g")
    var newIngredient : Ingredient;

    with(coffeeMachine) {
        options.forEach {
            println("Write how many ${it.value} of ${it.key} you want to add:")
            newIngredient = Ingredient(it.key, readln().toInt(), it.value)
            addIngredient(newIngredient)
        }

        println("Write how many disposable cups you want to add: ")
        addCups(readln().toInt());
    }
}



fun displayResources(shop: Shop, coffeeMachine: CoffeeMachine) {
    println(coffeeMachine)
    println("$${shop.cashAmount.toInt()} of money\n")
}

fun main() {

    val ingredient = mapOf("water" to "ml", "milk" to "ml", "coffee beans" to "g")
    val coffeeMachine = CoffeeMachine()
    val shop =  Shop(550.0)


    coffeeMachine.addIngredient(Ingredient("water", 400,"ml"),)
    coffeeMachine.addIngredient(Ingredient("milk", 540,"ml"),)
    coffeeMachine.addIngredient(Ingredient("coffee beans", 120,"g"),)


    displayResources(shop, coffeeMachine)
    println("Write action (buy, fill, take):")

    when(readln()) {
        "buy" -> buy(coffeeMachine,shop)
        "fill" -> fill(coffeeMachine)
        "take" -> println("I gave you $${shop.takeCash().toInt()}\n")
    }

    displayResources(shop, coffeeMachine)


}
