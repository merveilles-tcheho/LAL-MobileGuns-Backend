package com.nlmk.LAL.MobileGuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.Coil;

import jakarta.transaction.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<Coil, Integer> {

    // INSERT traçabilité MATMOD
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO TOOLS.TTQ004_TRANSACTIONS " +
                   "(CTRANSACTION_NUM, CPROCESS_NAME, CHOST_NAME, " +
                   "CKEY, CTABLE, CPARAMETER, CINSERT_DATE, " +
                   "CSTATUS, CINIT_PROCESS, CCODE_ACTION) " +
                   "VALUES (TOOLS.STQ004_TRANSACTION.NEXTVAL, " +
                   "'MAJ_UGFAB_ONE', 'GESFAB', :coilSq, " +
                   "'T001COILS', 'PROC_ONE_MATMOD', " +
                   "SYSDATE, NULL, 'GUN_MDL', 'I')",
                   nativeQuery = true)
    void insertMatmod(@Param("coilSq") Integer coilSq);

    // INSERT traçabilité MATALLOC (conditionnement NON)
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO TOOLS.TTQ004_TRANSACTIONS " +
                   "(CTRANSACTION_NUM, CPROCESS_NAME, CHOST_NAME, " +
                   "CKEY, CTABLE, CPARAMETER, CINSERT_DATE, " +
                   "CSTATUS, CINIT_PROCESS, CCODE_ACTION) " +
                   "VALUES (TOOLS.STQ004_TRANSACTION.NEXTVAL, " +
                   "'MAJ_UGFAB_ONE', 'GESFAB', :coilSq, " +
                   "'T001COILS', 'PROC_ONE_MATALLOC', " +
                   "SYSDATE, NULL, 'GUN_MDL', 'I')",
                   nativeQuery = true)
    void insertMatalloc(@Param("coilSq") Integer coilSq);

    // INSERT traçabilité MATPROD (conditionnement OUI + 1er emballage)
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO TOOLS.TTQ004_TRANSACTIONS " +
                   "(CTRANSACTION_NUM, CPROCESS_NAME, CHOST_NAME, " +
                   "CKEY, CTABLE, CPARAMETER, CINSERT_DATE, " +
                   "CSTATUS, CINIT_PROCESS, CCODE_ACTION) " +
                   "VALUES (TOOLS.STQ004_TRANSACTION.NEXTVAL, " +
                   "'MAJ_UGFAB_ONE', 'GESFAB', :coilSq, " +
                   "'PROC_TMT_PROC_ONE_MATPROD_STR', :param, " +
                   "SYSDATE, NULL, 'GUN_MDL', 'I')",
                   nativeQuery = true)
    void insertMatprod(@Param("coilSq") Integer coilSq,
                       @Param("param") String param);
}