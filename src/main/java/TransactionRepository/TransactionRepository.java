package TransactionRepository;

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
}
