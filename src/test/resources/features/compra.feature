Feature: Compra de Produto

  Scenario: Comprar Sauce Labs Onesie
    Given que o usuário está na tela inicial
    When o usuário adiciona o produto "Sauce Labs Onesie" ao carrinho
    And o usuário procede para o checkout
    And o usuário completa o processo de checkout
    Then a compra deve ser concluída com sucesso



