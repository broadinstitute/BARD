
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.hibernate.Transaction
import bard.db.dictionary.*
import org.springframework.transaction.TransactionStatus


Element.withTransaction{ TransactionStatus tx ->
    for( y in 0..9){
    
        def element = Element.build()
        println(element.id)
        println(element.dump())
    }
    tx.setRollbackOnly()
}