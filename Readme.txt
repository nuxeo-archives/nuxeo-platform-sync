The client module providing synchronization feature. It stays on the offline read-only server.
The features provided:
- requests the tuples describing the current situation on server, providing a custom query 
- removes the documents no longer available on server
- adds the new documents
- updates the newer ones. For these, it requests the DocumentSnapshots, updates the properties and if needed it also requests the blobs.
- imports through restlet the vocabularies 
- imports through restlet the relations
- service facet

Known bugs:
- in the case of security wholes in the tree, the offline server can end up in an undefined state. For instance: user has read rights on Workpspace A, not on Folder B below and on Folder C below. The Folder C can't be created on offline server as Folder B is not supplied from server. 
- listener for updating text blobs doesn't work for imported versions

Author: rdarlea@nuxeo.com
