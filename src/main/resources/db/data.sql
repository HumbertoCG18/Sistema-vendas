INSERT INTO produto (id,descricao,preco_unitario) VALUES (10,'Televisor',2000.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (20,'Geladeira',3500.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (30,'Fogão',1200.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (40,'Lava Louça',1800.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (50,'Lava Roupas',2870.0);
-- Novos Produtos
INSERT INTO produto (id,descricao,preco_unitario) VALUES (60,'Micro ondas',600.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (70,'Ar Condicionado',2200.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (80,'Aspirador de Pó Robô',1500.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (90,'Cafeteira Expressa',850.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (100,'Liquidificador Turbo',250.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (110,'Batedeira Planetária Pro',700.0);

-- Adiciona a coluna 'listado' e o valor 'true' em todos os INSERTS
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (100,10,20,5,50,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (200,20,10,5,30,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (300,40,8,5,50,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (400,30,15,5,40,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (500,50,12,5,35,true);
-- Novos Itens de Estoque
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (600,60,15,5,30,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (700,70,8,3,20,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (800,80,12,4,25,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (900,90,20,5,40,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (1000,100,25,10,50,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (1100,110,10,3,20,true);
