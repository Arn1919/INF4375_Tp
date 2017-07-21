//
////INSERE MULTILINESTRING
//
//INSERT INTO pistes (id,
//    type_voie1,
//    type_voie2,
//    longueur,
//    nbr_voie,
//    nom_arr_ville,
//    ligne  )
//VALUES(1, 1, 1, 1, 1, 'ABC', ST_GeomFromText('MULTILINESTRING((-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932))'));
//
//// DU GEOJSON
//INSERT INTO pistes 
//(
//    id,
//    type_voie1,
//    type_voie2,
//    longueur,
//    nbr_voie,
//    nom_arr_ville,
//    ligne  )
//VALUES(2, 1, 1, 1, 1, 'ABC', 
//ST_GeomFromText('MULTILINESTRING((300752.31081339833 5038993.2588551324, 300737.69477276254 5038992.2459647004, 300731.48032361676 5038991.7459280761, 300725.62282739632 5038991.031628089, 300718.69401201332 5038990.6030347794, 300710.47941413458 5038989.031514883))')
//);
//// DU GEOJSON (TRANSFORM)
//INSERT INTO pistes 
//(
//    id,
//    type_voie1,
//    type_voie2,
//    longueur,
//    nbr_voie,
//    nom_arr_ville,
//    ligne  )
//VALUES(3, 1, 1, 1, 1, 'ABC', 
//ST_Transform(
//ST_GeomFromText(
//'MULTILINESTRING((300752.31081339833 5038993.2588551324, 300737.69477276254 5038992.2459647004, 300731.48032361676 5038991.7459280761, 300725.62282739632 5038991.031628089, 300718.69401201332 5038990.6030347794, 300710.47941413458 5038989.031514883))',2950
//)
//, 4326)
//);
//// ST_Buffer
//SELECT 
//ST_AsText(
//    ST_Buffer(
//        ST_MakePoint(45.50894093, -73.56863737)::geography, 
//        2000
//    )::geography
//);
//
//// STD_Within
//SELECT id, ST_DWithin(ligne, ST_MakePoint(45.50894093, -73.56863737)::geography, 1000000000000000000000)
//FROM pistes
//WHERE id = 3;
//
//SELECT id, 
//ST_DWithin(
//    ligne::geography, 
//    ST_MakePoint(45.50894093, -73.56863737, 0.0)::geography,
//    10000
//)
//FROM pistes
//WHERE id = 3;
//        
//// ST_Intersection   
//SELECT id,
//ST_AsText(
//    ST_Intersection(
//        ligne::geography,
//        ST_Buffer(
//            ST_MakePoint(45.50894093, -73.56863737)::geography,       
//            20000
//        )::geography
//    )
//)
//FROM pistes;
//
//SELECT id,
//ST_AsText(
//    ST_Intersection(
//        ligne::geometry, 
//        ST_Buffer(
//            ST_MakePoint(45.50894093, -73.56863737, 0.0)::geography,
//            20000
//        )::geometry
//    )
//)
//FROM pistes
//WHERE id = 3;
//
//// ST_Intersects
//SELECT id,
//
//    ST_Intersects(
//        ligne::geometry, 
//        ST_Buffer(
//            ST_MakePoint(45.50894093, -73.56863737, 0.0)::geography,
//            20000
//        )::geometry
//    )
//FROM pistes
//WHERE id = 3;
//
//SELECT id,
//
//    ST_Intersects(
//        ST_MakePoint(45.50894093, -73.56863737, 0.0)::geography,   
//        ST_Buffer(
//            ligne::geometry, 
//            20000
//        )::geometry
//    )
//FROM pistes
//WHERE id = 3;
//
//// ST_Length
//SELECT ST_Length(ST_MakePoint(45.50894093, -73.56863737)) AS deg,
//       ST_Length(ST_MakePoint(45.50894093, -73.56863737)::geography)/1000 AS km;
//
//
//
//
//
//// UPDATE SRID OF GEOMETRY (???)
//SELECT UpdateGeometrySRID('pistes','ligne',4326);
//
//// EX GEOJSON
//
//
//{ "type": "Feature", "properties": 
//{ "ID": 4.000000, "ID_TRC_GEO": 1010521.000000, "TYPE_VOIE": 3.000000, "TYPE_VOIE2": 31.000000, "LONGUEUR": 46.000000, "NBR_VOIE": 1.000000, "SEPARATEUR": null, "SAISONS4": "NON", "PROTEGE_4S": "NON", "Ville_MTL": "OUI", "NOM_ARR_VI": "Ahuntsic-Cartierville" }, 
//"geometry": 
//{ "type": "MultiLineString", 
//"coordinates": 
//[ 
//[
//[ 287779.05321045732, 5043976.7833007919, 0.0 ], 
//[ 287744.43583595392, 5044007.6492099715, 0.0 ] 
//] 
//] } },
//
//
//"geometry": 
//{ "type": "MultiLineString", 
//"coordinates": 
//[
//[
//[ 300752.31081339833, 5038993.2588551324, 0.0 ],
//[ 300737.69477276254, 5038992.2459647004, 0.0 ],
//[ 300731.48032361676, 5038991.7459280761, 0.0 ],
//[ 300725.62282739632, 5038991.031628089, 0.0 ],
//[ 300718.69401201332, 5038990.6030347794, 0.0 ],
//[ 300710.47941413458, 5038989.031514883, 0.0 ] 
//] 
//] } },
//
//MULTILINESTRING(300752.31081339833 5038993.2588551324, 300737.69477276254 5038992.2459647004, 300731.48032361676 5038991.7459280761, 300725.62282739632 5038991.031628089, 300718.69401201332 5038990.6030347794, 300710.47941413458 5038989.031514883)
//
//        select
//               activities.id
//               , name
//               , description
//               , district
//               , venue_name
//               , ST_X(coordinates::geometry) AS lat
//               , ST_Y(coordinates::geometry) AS lng
//        from
//            activities
//        inner join
//            activities_date
//        on
//            activities_date.id = activities.id
//        where
//            ST_DWithin(Geography(coordinates), Geography(ST_MakePoint(45.50894093, -73.56863737)) , 5000);
//               
//        
//        select
//               activities.id
//               , name
//               , description
//               , district
//               , venue_name
//               , ST_X(coordinates::geometry) AS lat
//               , ST_Y(coordinates::geometry) AS lng
//        from
//            activities
//        inner join
//            activities_date
//        on
//            activities_date.id = activities.id
//        where
//            event_date >= '2017-01-01'
//        and
//            event_date <= '2017-12-15';
//
//
//DELETE FROM activities_date
//WHERE event_id = 36;
//DELETE FROM activities
//WHERE id = 36;
//
//  
//             UPDATE activities
//             SET
//                   name = 'test'
//                 , description = 'test'
//                 , district = 'test'
//                 , venue_name = 'test'
//                 , coordinates = ST_MakePoint(0.0, 0.0)
//             WHERE
//                 id = 279;
//      
//















