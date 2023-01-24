print("basic long scenario")
with open('src/test/resources/scenarios/basic_long_scenario.txt', 'w') as input_file:
    order_id = 0
    for round in range(10_000_000):
        input_file.write(f'{order_id},B,100,1000\n')
        order_id += 1
        input_file.write(f'{order_id},B,99,2000\n')
        order_id += 1
        input_file.write(f'{order_id},B,98,5000\n')
        order_id += 1
        input_file.write(f'{order_id},S,97,8000\n')
        order_id += 1
        if round % 500_000 == 0:
            print(round)


print("many orders scenario")
with open('src/test/resources/scenarios/many_orders_scenario.txt', 'w') as input_file:
    order_id = 0
    mid_price = 20_000
    for round in range(50):
        print('round =', round)
        order_volume = 300
        # add orders
        for price_offset in range(10_000):
            for order_per_level in range(100):
                input_file.write(f'{order_id},B,{mid_price - price_offset},{order_volume}\n')
                order_id += 1

                input_file.write(f'{order_id},S,{mid_price + price_offset},{order_volume}\n')
                order_id += 1
        # match orders
        for price_offset in range(10_000):
            for order_per_level in range(100):
                input_file.write(f'{order_id},B,{mid_price + price_offset},{order_volume}\n')
                order_id += 1

                input_file.write(f'{order_id},S,{mid_price - price_offset},{order_volume}\n')
                order_id += 1