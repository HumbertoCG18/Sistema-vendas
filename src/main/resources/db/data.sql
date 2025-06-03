INSERT INTO produto (id,descricao,preco_unitario) VALUES (10,'Televisor',2000.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (20,'Geladeira',3500.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (30,'Fogao',1200.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (40,'Lava-louça',1800.0);
INSERT INTO produto (id,descricao,preco_unitario) VALUES (50,'lava-roupas',2870.0);

-- Adiciona a coluna 'listado' e o valor 'true' em todos os INSERTS
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (100,10,20,5,50,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (200,20,10,5,30,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (300,40,8,5,50,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (400,30,15,5,40,true);
INSERT INTO item_de_estoque (id, produto_id, quantidade, estoque_min, estoque_max, listado ) VALUES (500,50,12,5,35,true);