
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.hibernate.Transaction
import bard.db.registration.*



SessionFactory sessionFactory = ctx.sessionFactory
Session session = sessionFactory.openSession()
Transaction tx = session.beginTransaction()

assert 4 == Assay.count()
try{
    println(tx.properties)
    Assay assay = Assay.build()
    assay.save(flush:true)
    assert 5 == Assay.count()

    MeasureContext mc = MeasureContext.build()
    mc.assay = assay
    mc.save(flush:true)
    tx.rollback()
}
catch(Exception e)
{
    println(e)
    tx.rollback()
}
finally{
    if(tx.active){
        tx.rollback()
    }
}
assert 4 == Assay.count()