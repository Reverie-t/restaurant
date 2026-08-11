import { defineStore } from "pinia"

export const useCartStore = defineStore("cart", {

  state: () => ({

    items: JSON.parse(
      localStorage.getItem("restaurant-cart") || "[]"
    )

  }),

  getters: {

    totalQuantity(state) {

      return state.items.reduce(
        (total, item) =>
          total + item.quantity,
        0
      )

    },

    totalAmount(state) {

      return state.items.reduce(
        (total, item) => {

          return total +
            Number(item.price) *
            item.quantity

        },
        0
      )

    }

  },

  actions: {

    save() {

      localStorage.setItem(
        "restaurant-cart",
        JSON.stringify(this.items)
      )

    },


    addItem(dish, quantity = 1) {

      const existing =
        this.items.find(
          item => item.id === dish.id
        )


      if (existing) {

        existing.quantity += quantity

      } else {

        this.items.push({

          id: dish.id,

          name: dish.name,

          price: Number(dish.price),

          image: dish.image,

          categoryName: dish.categoryName,

          quantity

        })

      }


      this.save()

    },


    removeItem(id) {

      this.items =
        this.items.filter(
          item => item.id !== id
        )

      this.save()

    },


    increase(id) {

      const item =
        this.items.find(
          item => item.id === id
        )

      if (item) {

        item.quantity++

        this.save()

      }

    },


    decrease(id) {

      const item =
        this.items.find(
          item => item.id === id
        )

      if (!item) {
        return
      }


      if (item.quantity > 1) {

        item.quantity--

      } else {

        this.removeItem(id)

        return

      }


      this.save()

    },


    clear() {

      this.items = []

      this.save()

    }

  }

})
