
This module provides a client/server architecture to synchronize 2 nuxeo servers together.

This is a master / slave architecture where the replicated slave is supposed to be read-only since there is no conflict management system.

The sync process covers :
- request the tuples describing the current situation on server (using a customizable query to define the scope)
- synchronize docs
  - removes the documents no longer available on server
  - adds the new documents
  - updates the newer ones. For these, it requests the DocumentSnapshots, updates the properties and if needed it also requests the blobs.
- imports through restlet the vocabularies 
- imports through restlet the relations

Limitations : the current implementation does not synchronize Audit records.

